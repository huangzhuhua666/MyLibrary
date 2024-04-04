#!/bin/bash -e

localRepoPath="../custom-gradle-plugin-repo"
pluginGroup="com.example.plugin"
pluginArtifact="library-gradle-plugin"

gradlePluginPath="$localRepoPath/${pluginGroup//.//}/$pluginArtifact"
#匹配版本号
versionMatcher="^[0-9]+(\.[0-9]+)+$"
#获取最新版本号
latestVersion=$(ls $gradlePluginPath | grep -oE "$versionMatcher" | sort -rn | head -n 1)

for version in $(ls ""$gradlePluginPath"" | grep -oE "$versionMatcher" | grep -v "$latestVersion"); do
    rm -rf "$gradlePluginPath/$version"
done