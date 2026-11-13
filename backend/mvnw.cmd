@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script (Windows)
@REM
@REM NOTE: The wrapper downloads Maven automatically the first time it runs
@REM (internet connection required). Alternatively, open the `backend` folder
@REM directly in IntelliJ IDEA and use its bundled Maven - no wrapper needed.
@REM ----------------------------------------------------------------------------
@ECHO OFF

SET BASE_DIR=%~dp0
SET WRAPPER_JAR=%BASE_DIR%.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_PROPERTIES=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties

IF NOT EXIST "%WRAPPER_JAR%" (
  echo Maven Wrapper jar not found. Please open this project in IntelliJ IDEA
  echo and let it use the bundled Maven installation, or install Maven manually.
  exit /b 1
)

IF "%JAVA_HOME%"=="" (
  SET JAVA_EXE=java
) ELSE (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
