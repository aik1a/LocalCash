@echo off
setlocal EnableExtensions
set "REPO=%USERPROFILE%\LocalCash-firebase"
echo Publicar LocalCash MVP en una rama nueva, sin modificar main.
echo Repositorio local: %REPO%
if not exist "%REPO%\.git" (
  echo No se encontro el clon. Este script espera la carpeta LocalCash-firebase de tu usuario.
  pause
  exit /b 1
)
if not exist "%~dp0LocalCash-mvp.bundle" (
  echo Extrae todo el ZIP antes de ejecutar este archivo.
  pause
  exit /b 1
)
cd /d "%REPO%"
for /f "delims=" %%R in ('git remote get-url origin 2^>nul') do set "ORIGIN=%%R"
if /i not "%ORIGIN%"=="https://github.com/aik1a/LocalCash.git" (
  echo El remoto no coincide con aik1a/LocalCash. No se modifico nada.
  pause
  exit /b 1
)
for /f "delims=" %%S in ('git status --porcelain') do (
  echo Hay cambios locales sin guardar. No se reemplazaron ni borraron archivos.
  echo Guarda esos cambios antes de volver a ejecutar el script.
  pause
  exit /b 1
)
git bundle verify "%~dp0LocalCash-mvp.bundle"
if errorlevel 1 goto error
git fetch "%~dp0LocalCash-mvp.bundle" refs/heads/task/FB-mvp-delivery:refs/heads/task/FB-mvp-delivery
if errorlevel 1 goto error
git switch task/FB-mvp-delivery
if errorlevel 1 goto error
if not exist "app\google-services.json" if exist "%~dp0google-services.json" copy /y "%~dp0google-services.json" "app\google-services.json" >nul
git push -u origin task/FB-mvp-delivery
if errorlevel 1 goto error
echo.
echo Publicacion completada en task/FB-mvp-delivery. No se fusiono main.
echo https://github.com/aik1a/LocalCash/tree/task/FB-mvp-delivery
pause
exit /b 0
:error
echo.
echo Git no pudo completar la operacion. Revisa el mensaje anterior.
echo No se ejecutaron force-push, borrados ni reset.
pause
exit /b 1
