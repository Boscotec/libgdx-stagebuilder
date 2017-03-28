# StageBuilder

This is a library project for building LibGDX stages (screens) from xml files.

1. [Introduction](https://github.com/peakgames/libgdx-stagebuilder/wiki/Introduction)
2. [Basic Scene2d Widgets](https://github.com/peakgames/libgdx-stagebuilder/wiki/Basic-Scene2d-Widgets)
3. [Widget Groups](https://github.com/peakgames/libgdx-stagebuilder/wiki/Widget-Groups)
4. [Reusing Widget Groups](https://github.com/peakgames/libgdx-stagebuilder/wiki/Reusing-Widget-Groups)
5. [Custom Widgets](https://github.com/peakgames/libgdx-stagebuilder/wiki/Custom-Widgets)
6. [AbstractGame & AbstractScreen](https://github.com/peakgames/libgdx-stagebuilder/wiki/AbstractGame-&-AbstractScreen)
7. [Multiple Screen Size Support](https://github.com/peakgames/libgdx-stagebuilder/wiki/Multiple-Screen-Size-Support)
8. [Updating Layouts At Runtime](https://github.com/peakgames/libgdx-stagebuilder/wiki/Updating-Layouts-At-Runtime)
9. [Handling Screen Orientation Changes](https://github.com/peakgames/libgdx-stagebuilder/wiki/Handling-Screen-Orientation-Changes)
10. [Demo Application](https://github.com/peakgames/libgdx-stagebuilder/wiki/Demo-Application)
11. [Obtaining](https://github.com/peakgames/libgdx-stagebuilder/wiki/Obtaining)
12. [Games Using StageBuilder](https://github.com/peakgames/libgdx-stagebuilder/wiki/Games-Using-StageBuilder)
13. [Kotlin Type-Safe Builders (DSL)](https://github.com/peakgames/libgdx-stagebuilder/wiki/Kotlin-Type-Safe-Builder)

## Kotlin Type-Safe Builders

If you are using [Kotlin](https://kotlinlang.org/) you can benefit type-safe builders (DSL) for creating screens and Scene2d actors.
To enable StageBuilder DSL you should add *stagebuilder-extensions* dependency

Gradle
```
compile 'com.github.peakgames.libgdx-stagebuilder:stagebuilder-extensions:$stagebuilder_version'
```

Maven
```
<dependency>
    <groupId>net.peakgames.libgdx</groupId>
    <artifactId>stagebuilder-extensions</artifactId>
    <version>$stagebuilder_version</version>
</dependency>
```



## Include via Jitpack

[![](https://jitpack.io/v/peakgames/libgdx-stagebuilder.svg)](https://jitpack.io/#peakgames/libgdx-stagebuilder)

Gradle;
```gradle
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}

    ...

	dependencies {
	        compile 'com.github.peakgames.libgdx-stagebuilder:stagebuilder-core:v3.19.0'
	}
```

Maven;
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

    ...

	<dependency>
        <groupId>com.github.peakgames.libgdx-stagebuilder</groupId>
        <artifactId>stagebuilder-core</artifactId>
        <version>v3.19.0</version>
    </dependency>
```
