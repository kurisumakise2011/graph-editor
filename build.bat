@echo off
java -version 1>nul 2>nul || (
   echo no java installed
   exit /b 2
)
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k%%l%%m"

if %jver% LSS 11 (
  echo java version is too low
  echo at least 11 is needed
  exit /b 1
)

del nul

call mvn clean install -Dmaven.test.skip=true