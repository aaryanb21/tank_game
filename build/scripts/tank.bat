@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  tank startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and TANK_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\tank-1.0.jar;%APP_HOME%\lib\guava-28.0-jre.jar;%APP_HOME%\lib\core-3.3.7.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-2.8.1.jar;%APP_HOME%\lib\error_prone_annotations-2.3.2.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.17.jar;%APP_HOME%\lib\gluegen-rt-main-2.3.2.jar;%APP_HOME%\lib\jogl-2.3.2.jar;%APP_HOME%\lib\jogl-all-main-2.3.2.jar;%APP_HOME%\lib\newt-main-2.3.2.jar;%APP_HOME%\lib\nativewindow-main-2.3.2.jar;%APP_HOME%\lib\gluegen-rt-2.3.2.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-android-aarch64.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-android-armv6.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-linux-amd64.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-linux-armv6.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-linux-armv6hf.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-linux-i586.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-macosx-universal.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-solaris-amd64.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-solaris-i586.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-windows-amd64.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-windows-i586.jar;%APP_HOME%\lib\jogl-all-2.3.2.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-android-aarch64.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-android-armv6.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-linux-amd64.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-linux-armv6.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-linux-armv6hf.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-linux-i586.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-macosx-universal.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-solaris-amd64.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-solaris-i586.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-windows-amd64.jar;%APP_HOME%\lib\jogl-all-2.3.2-natives-windows-i586.jar;%APP_HOME%\lib\newt-2.3.2.jar;%APP_HOME%\lib\newt-2.3.2-event.jar;%APP_HOME%\lib\newt-2.3.2-ogl.jar;%APP_HOME%\lib\newt-2.3.2-driver-android.jar;%APP_HOME%\lib\newt-2.3.2-driver-bcm-old.jar;%APP_HOME%\lib\newt-2.3.2-driver-bcm-vc.jar;%APP_HOME%\lib\newt-2.3.2-driver-intelgdl.jar;%APP_HOME%\lib\newt-2.3.2-driver-kd.jar;%APP_HOME%\lib\newt-2.3.2-driver-linux.jar;%APP_HOME%\lib\newt-2.3.2-driver-osx.jar;%APP_HOME%\lib\newt-2.3.2-driver-win.jar;%APP_HOME%\lib\newt-2.3.2-driver-x11.jar;%APP_HOME%\lib\newt-2.3.2-natives-android-aarch64.jar;%APP_HOME%\lib\newt-2.3.2-natives-android-armv6.jar;%APP_HOME%\lib\newt-2.3.2-natives-linux-amd64.jar;%APP_HOME%\lib\newt-2.3.2-natives-linux-armv6.jar;%APP_HOME%\lib\newt-2.3.2-natives-linux-armv6hf.jar;%APP_HOME%\lib\newt-2.3.2-natives-linux-i586.jar;%APP_HOME%\lib\newt-2.3.2-natives-macosx-universal.jar;%APP_HOME%\lib\newt-2.3.2-natives-solaris-amd64.jar;%APP_HOME%\lib\newt-2.3.2-natives-solaris-i586.jar;%APP_HOME%\lib\newt-2.3.2-natives-windows-amd64.jar;%APP_HOME%\lib\newt-2.3.2-natives-windows-i586.jar;%APP_HOME%\lib\nativewindow-2.3.2.jar;%APP_HOME%\lib\nativewindow-2.3.2-os-win.jar;%APP_HOME%\lib\nativewindow-2.3.2-os-osx.jar;%APP_HOME%\lib\nativewindow-2.3.2-os-x11.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-android-aarch64.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-android-armv6.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-linux-amd64.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-linux-armv6.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-linux-armv6hf.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-linux-i586.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-macosx-universal.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-solaris-amd64.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-solaris-i586.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-windows-amd64.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-windows-i586.jar


@rem Execute tank
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %TANK_OPTS%  -classpath "%CLASSPATH%" Tanks.App %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable TANK_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%TANK_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
