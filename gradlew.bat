@echo off
setlocal

set APP_HOME=%~dp0
set WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

if not exist "%WRAPPER_JAR%" (
  echo Missing "%WRAPPER_JAR%" 1>&2
  exit /b 1
)

set JAVA_BIN=java
if defined JAVA_HOME (
  if exist "%JAVA_HOME%\bin\java.exe" (
    set JAVA_BIN=%JAVA_HOME%\bin\java
  )
)

"%JAVA_BIN%" -jar "%WRAPPER_JAR%" %*
endlocal

