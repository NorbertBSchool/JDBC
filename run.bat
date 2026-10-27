@echo off
cd /d "%~dp0"
if not exist out\production\EmployeesManagerSwing mkdir out\production\EmployeesManagerSwing
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.4.0.jar" -d out\production\EmployeesManagerSwing src\*.java
if errorlevel 1 pause
java -cp "out\production\EmployeesManagerSwing;lib\mysql-connector-j-8.4.0.jar" Main
pause
