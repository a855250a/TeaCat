@echo off
setlocal
cd /d "%~dp0"
echo Checking for legacy PetHealth names...
findstr /S /I /N /P /C:"PetHealthCloud" /C:"pethealthcloud" /C:"PetHealth" *.java *.properties *.xml *.html *.js *.css *.md *.txt *.http 2>nul
if %errorlevel%==0 (
  echo.
  echo [FAIL] Legacy PetHealth text was found above.
  exit /b 1
) else (
  echo [PASS] No legacy PetHealth naming found in source text.
)
echo.
echo Expected application class:
if exist "src\main\java\com\teacat\TeaCatApplication.java" (
  echo [PASS] src\main\java\com\teacat\TeaCatApplication.java
) else (
  echo [FAIL] TeaCatApplication.java missing.
  exit /b 1
)
endlocal
