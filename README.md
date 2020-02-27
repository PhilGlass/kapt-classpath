# Kapt classpath changes

Demonstrates that the Gradle & Kapt implementations of incremental annotation processing have different behaviour when an ABI-incompatible change is made to the classpath (Gradle will re-run all processors, Kapt will not).

This means that annotation processors which scan the classpath (e.g. using `Elements.getPackageElement()` to list all the classes in a well-known package) will not run correctly under Kapt. A public example of such a processor is Crumb: https://github.com/uber/crumb/commits/master

## ABI-compatible classpath change
Adds a new private method to an existing public class.

### Gradle (not re-run)
1) `git clean -xdf`
2) `./gradlew feature-java:assemble --no-build-cache --console=plain`

    ```
    > Task :feature-java:processResources NO-SOURCE
    > Task :processors:processResources
    > Task :common:compileJava
    > Task :annotations:compileJava
    > Task :annotations:processResources NO-SOURCE
    > Task :annotations:classes
    > Task :annotations:jar
    > Task :processors:compileJava
    > Task :processors:classes
    > Task :processors:jar

    > Task :feature-java:compileJava
    warning: Running ISOLATING processor on [feature.java.AnnotatedWithIsolating]
    warning: Running AGGREGATING processor on [feature.java.AnnotatedWithAggregating]
    warning: Running ISOLATING processor on []
    warning: Running AGGREGATING processor on []
    4 warnings

    > Task :feature-java:classes
    > Task :feature-java:jar
    > Task :feature-java:assemble
    Kotlin build report is written to ...

    BUILD SUCCESSFUL in 646ms
    8 actionable tasks: 8 executed
    ```

3) `printf "package common;\n\npublic class SomeType {\n    private void something() {}\n}\n" > common/src/main/java/common/SomeType.java`
4) `./gradlew feature-java:assemble --no-build-cache --console=plain`

    ```
    > Task :feature-java:processResources NO-SOURCE
    > Task :processors:processResources UP-TO-DATE
    > Task :annotations:compileJava UP-TO-DATE
    > Task :annotations:processResources NO-SOURCE
    > Task :annotations:classes UP-TO-DATE
    > Task :annotations:jar UP-TO-DATE
    > Task :processors:compileJava UP-TO-DATE
    > Task :processors:classes UP-TO-DATE
    > Task :processors:jar UP-TO-DATE
    > Task :common:compileJava
    > Task :feature-java:compileJava UP-TO-DATE
    > Task :feature-java:classes UP-TO-DATE
    > Task :feature-java:jar UP-TO-DATE
    > Task :feature-java:assemble UP-TO-DATE
    Kotlin build report is written to ...

    BUILD SUCCESSFUL in 563ms
    8 actionable tasks: 1 executed, 7 up-to-date
    ```

### Kapt (not re-run)
1) `git clean -xdf`
2) `./gradlew feature-kotlin:assemble --no-build-cache --console=plain`

    ```
    > Task :feature-kotlin:processResources NO-SOURCE
    > Task :processors:processResources
    > Task :common:compileJava
    > Task :common:processResources NO-SOURCE
    > Task :common:classes
    > Task :annotations:compileJava
    > Task :annotations:processResources NO-SOURCE
    > Task :annotations:classes
    > Task :common:jar
    > Task :annotations:jar
    > Task :processors:compileJava
    > Task :processors:classes
    > Task :processors:jar
    > Task :feature-kotlin:kaptGenerateStubsKotlin

    > Task :feature-kotlin:kaptKotlin
    w: warning: Running ISOLATING processor on [feature.kotlin.AnnotatedWithIsolating]
    w: warning: Running AGGREGATING processor on [feature.kotlin.AnnotatedWithAggregating]
    w: warning: Running ISOLATING processor on []
    w: warning: Running AGGREGATING processor on []

    > Task :feature-kotlin:compileKotlin
    > Task :feature-kotlin:compileJava NO-SOURCE
    > Task :feature-kotlin:classes UP-TO-DATE
    > Task :feature-kotlin:inspectClassesForKotlinIC
    > Task :feature-kotlin:jar
    > Task :feature-kotlin:assemble
    Kotlin build report is written to ...

    BUILD SUCCESSFUL in 5s
    12 actionable tasks: 12 executed
    ```

3) `printf "package common;\n\npublic class SomeType {\n    private void something() {}\n}\n" > common/src/main/java/common/SomeType.java`
4) `./gradlew feature-kotlin:assemble --no-build-cache --console=plain`

    ```
    > Task :feature-kotlin:processResources NO-SOURCE
    > Task :processors:processResources UP-TO-DATE
    > Task :annotations:compileJava UP-TO-DATE
    > Task :common:compileJava UP-TO-DATE
    > Task :annotations:processResources NO-SOURCE
    > Task :annotations:classes UP-TO-DATE
    > Task :common:processResources NO-SOURCE
    > Task :common:classes UP-TO-DATE
    > Task :annotations:jar UP-TO-DATE
    > Task :common:jar UP-TO-DATE
    > Task :processors:compileJava UP-TO-DATE
    > Task :processors:classes UP-TO-DATE
    > Task :processors:jar UP-TO-DATE
    > Task :feature-kotlin:kaptGenerateStubsKotlin UP-TO-DATE
    > Task :feature-kotlin:kaptKotlin UP-TO-DATE
    > Task :feature-kotlin:compileKotlin UP-TO-DATE
    > Task :feature-kotlin:compileJava NO-SOURCE
    > Task :feature-kotlin:classes UP-TO-DATE
    > Task :feature-kotlin:inspectClassesForKotlinIC UP-TO-DATE
    > Task :feature-kotlin:jar UP-TO-DATE
    > Task :feature-kotlin:assemble UP-TO-DATE
    Kotlin build report is written to ...

    BUILD SUCCESSFUL in 527ms
    12 actionable tasks: 12 up-to-date
    ```

## ABI-incompatible classpath change
Adds a new public class.

### Gradle (re-run)
1) `git clean -xdf`
2) `./gradlew feature-java:assemble --no-build-cache --console=plain`

    ```
    > Task :feature-java:processResources NO-SOURCE
    > Task :processors:processResources
    > Task :common:compileJava
    > Task :annotations:compileJava
    > Task :annotations:processResources NO-SOURCE
    > Task :annotations:classes
    > Task :annotations:jar
    > Task :processors:compileJava
    > Task :processors:classes
    > Task :processors:jar

    > Task :feature-java:compileJava
    warning: Running ISOLATING processor on [feature.java.AnnotatedWithIsolating]
    warning: Running AGGREGATING processor on [feature.java.AnnotatedWithAggregating]
    warning: Running ISOLATING processor on []
    warning: Running AGGREGATING processor on []
    4 warnings

    > Task :feature-java:classes
    > Task :feature-java:jar
    > Task :feature-java:assemble
    Kotlin build report is written to ...

    BUILD SUCCESSFUL in 630ms
    8 actionable tasks: 8 executed
    ```

3) `printf "package common;\n\npublic class SomeType2 {\n}\n" > common/src/main/java/common/SomeType2.java`
4) `./gradlew feature-java:assemble --no-build-cache --console=plain`

    ```
    > Task :feature-java:processResources NO-SOURCE
    > Task :processors:processResources UP-TO-DATE
    > Task :annotations:compileJava UP-TO-DATE
    > Task :annotations:processResources NO-SOURCE
    > Task :annotations:classes UP-TO-DATE
    > Task :annotations:jar UP-TO-DATE
    > Task :processors:compileJava UP-TO-DATE
    > Task :processors:classes UP-TO-DATE
    > Task :processors:jar UP-TO-DATE
    > Task :common:compileJava

    > Task :feature-java:compileJava
    warning: Running ISOLATING processor on [feature.java.AnnotatedWithIsolating]
    warning: Running AGGREGATING processor on [feature.java.AnnotatedWithAggregating]
    warning: Running ISOLATING processor on []
    warning: Running AGGREGATING processor on []
    4 warnings

    > Task :feature-java:classes
    > Task :feature-java:jar UP-TO-DATE
    > Task :feature-java:assemble UP-TO-DATE
    Kotlin build report is written to ...

    BUILD SUCCESSFUL in 634ms
    8 actionable tasks: 2 executed, 6 up-to-date
    ```
    
### Kapt (not re-run)
1) `git clean -xdf`
2) `./gradlew feature-kotlin:assemble --no-build-cache --console=plain`

    ```
    > Task :feature-kotlin:processResources NO-SOURCE
    > Task :processors:processResources
    > Task :common:compileJava
    > Task :annotations:compileJava
    > Task :annotations:processResources NO-SOURCE
    > Task :common:processResources NO-SOURCE
    > Task :annotations:classes
    > Task :common:classes
    > Task :common:jar
    > Task :annotations:jar
    > Task :processors:compileJava
    > Task :processors:classes
    > Task :processors:jar
    > Task :feature-kotlin:kaptGenerateStubsKotlin

    > Task :feature-kotlin:kaptKotlin
    w: warning: Running ISOLATING processor on [feature.kotlin.AnnotatedWithIsolating]
    w: warning: Running AGGREGATING processor on [feature.kotlin.AnnotatedWithAggregating]
    w: warning: Running ISOLATING processor on []
    w: warning: Running AGGREGATING processor on []

    > Task :feature-kotlin:compileKotlin
    > Task :feature-kotlin:compileJava NO-SOURCE
    > Task :feature-kotlin:classes UP-TO-DATE
    > Task :feature-kotlin:inspectClassesForKotlinIC
    > Task :feature-kotlin:jar
    > Task :feature-kotlin:assemble
    Kotlin build report is written to ...

    BUILD SUCCESSFUL in 1s
    12 actionable tasks: 12 executed
    ```

3) `printf "package common;\n\npublic class SomeType2 {\n}\n" > common/src/main/java/common/SomeType2.java`
4) `./gradlew feature-kotlin:assemble --no-build-cache --console=plain`

    ```
    > Task :feature-kotlin:processResources NO-SOURCE
    > Task :annotations:compileJava UP-TO-DATE
    > Task :processors:processResources UP-TO-DATE
    > Task :annotations:processResources NO-SOURCE
    > Task :annotations:classes UP-TO-DATE
    > Task :annotations:jar UP-TO-DATE
    > Task :processors:compileJava UP-TO-DATE
    > Task :processors:classes UP-TO-DATE
    > Task :processors:jar UP-TO-DATE
    > Task :common:compileJava
    > Task :common:processResources NO-SOURCE
    > Task :common:classes
    > Task :common:jar
    > Task :feature-kotlin:kaptGenerateStubsKotlin
    > Task :feature-kotlin:kaptKotlin
    > Task :feature-kotlin:compileKotlin
    > Task :feature-kotlin:compileJava NO-SOURCE
    > Task :feature-kotlin:classes UP-TO-DATE
    > Task :feature-kotlin:inspectClassesForKotlinIC UP-TO-DATE
    > Task :feature-kotlin:jar UP-TO-DATE
    > Task :feature-kotlin:assemble UP-TO-DATE
    Kotlin build report is written to ...

    BUILD SUCCESSFUL in 1s
    12 actionable tasks: 5 executed, 7 up-to-date
    ```
