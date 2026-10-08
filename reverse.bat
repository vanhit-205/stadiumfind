@echo off
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" reverse tcp:8080 tcp:8080
echo ========================================================
echo [StadiumFind] Da chuyen huong port 8080 thanh cong!
echo Dien thoai da co the goi backend qua localhost:8080
echo ========================================================
pause
