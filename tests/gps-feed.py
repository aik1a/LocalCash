"""Coordina únicamente el GPS simulado del emulador desde el host CI."""
import os
import pathlib
import signal
import subprocess
import sys
pidfile = pathlib.Path('evidence/gps-feed.pid')
if sys.argv[1] == 'start':
    with pathlib.Path('evidence/gps-feed.log').open('w') as log:
        process = subprocess.Popen(['bash', 'tests/location-gps-feed.sh'], stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
    pidfile.write_text(str(process.pid))
else:
    os.killpg(int(pidfile.read_text()), signal.SIGTERM)
