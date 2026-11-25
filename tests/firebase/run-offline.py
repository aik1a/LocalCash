#!/usr/bin/env python3
"""Recorrido sintético en emulador Android ya iniciado; no instala ni borra apps."""
import argparse
import pathlib
import subprocess
import uuid

parser = argparse.ArgumentParser()
parser.add_argument('--serial', required=True, help='Serial adb del emulador descartable')
parser.add_argument('--output', required=True, type=pathlib.Path)
args = parser.parse_args()
args.output.mkdir(parents=True, exist_ok=True)


def adb(*command):
    return subprocess.run(['adb', '-s', args.serial, *command], text=True,
                          stdout=subprocess.PIPE, stderr=subprocess.STDOUT, timeout=100)


probe = adb('shell', 'getprop', 'ro.kernel.qemu')
if probe.returncode or probe.stdout.strip() != '1':
    raise SystemExit('Solo se admite emulador Android descartable (ro.kernel.qemu=1).')
sha = subprocess.run(['git', 'rev-parse', 'HEAD'], text=True, capture_output=True, check=True).stdout.strip()
(args.output / 'source-sha.txt').write_text(sha + '\n')
(args.output / 'device.txt').write_text(adb('shell', 'getprop', 'ro.build.version.sdk').stdout)
run_id = 'f1-' + uuid.uuid4().hex
(args.output / 'run-id.txt').write_text(run_id + '\n')
for phase in ['prepare', 'offline', 'resumeOffline', 'reconnect', 'reject']:
    # Nueva instrumentación = nuevo proceso; conservar datos/caché del anterior.
    stopped = adb('shell', 'am', 'force-stop', 'com.aik1a.localcash')
    if stopped.returncode:
        raise SystemExit(stopped.stdout)
    result = adb('shell', 'am', 'instrument', '-w', '-r', '-e', 'phase', phase,
                 '-e', 'runId', run_id, '-e', 'mode', 'firestore',
                 'com.aik1a.localcash.test/com.aik1a.localcash.FlowChecks')
    (args.output / (phase + '.log')).write_text(result.stdout)
    (args.output / (phase + '.exit')).write_text(str(result.returncode) + '\n')
    if result.returncode or 'LOCALCASH_FIRESTORE_PASS ' + phase not in result.stdout:
        raise SystemExit('FAIL ' + phase + '\n' + result.stdout)
    print('PASS', phase)
print('PASS F1-T01: local pendiente, nuevo proceso offline, remoto y rechazo comprobados.')
