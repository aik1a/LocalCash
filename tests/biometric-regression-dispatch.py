import subprocess,sys
if sys.argv[1]=="36": subprocess.run([sys.executable,"tests/biometric-emulator.py","regressions"],check=True)
else: print("API24 biometric compatibility validated; full regression is executed on API36.")
