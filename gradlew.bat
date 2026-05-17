#!/usr/bin/env pwsh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for Windows
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a symlink
$scope = $ExecutionContext.SessionState.Module
$null = $scope.FunctionDefinitions.Remove('pop')
$null = $scope.FunctionDefinitions.Remove('push')
$null = $scope.Alias.Remove('pop')
$null = $scope.Alias.Remove('push')

if ($PSVersionTable.PSVersion -lt $(New-Object System.Version 6, 0)) {
    Write-Host 'PowerShell is version 5, this script requires version 6 or newer.'
    exit 1
}

$PSNativeCommandUseErrorActionPreference = $true
$ErrorActionPreference = 'Stop'

$APP_HOME = Split-Path -Parent $MyInvocation.MyCommand.Source

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
$DEFAULT_JVM_OPTS = '-Xmx64m', '-Xms64m'

# Collect all arguments for the java command
$CLASSPATH = "$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Use the maximum available, or set MAX_FD != maximum.
$MAX_FD = 'maximum'

function Find-Java {
    if ($JAVA_HOME -and (Test-Path "$JAVA_HOME/bin/java.exe")) {
        return "$JAVA_HOME/bin/java.exe"
    }

    $java = Get-Command -Name java.exe -ErrorAction SilentlyContinue
    if ($java) {
        return $java.Source
    }

    throw 'ERROR: JAVA_HOME is not set and no java.exe could be found in your PATH.'
}

$JAVACMD = Find-Java
$DEFAULT_JVM_OPTS += "-Dorg.gradle.appname=$($MyInvocation.MyCommand.Source)"

$JAVA_ARGS = @(
    $DEFAULT_JVM_OPTS,
    '-classpath',
    $CLASSPATH,
    'org.gradle.wrapper.GradleWrapperMain'
) + $args

& "$JAVACMD" @JAVA_ARGS
