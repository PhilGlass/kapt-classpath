## ABI-compatible classpath change
Adds a new private method to an existing public class.

### Gradle ✅
1) `git clean -xdf`
2) `./gradlew feature-java:assemble --no-build-cache`
    ```
    warning: Running ISOLATING processor on [feature.java.AnnotatedWithIsolating]
    warning: Running AGGREGATING processor on [feature.java.AnnotatedWithAggregating]
    warning: Running ISOLATING processor on []
    warning: Running AGGREGATING processor on []
    
    BUILD SUCCESSFUL in 621ms
    8 actionable tasks: 8 executed
    ```
3) `printf "package common;\n\npublic class SomeType {\n    private void something() {}\n}\n" > common/src/main/java/common/SomeType.java`
4) `./gradlew feature-java:assemble --no-build-cache`
    ```
    BUILD SUCCESSFUL in 516ms
    8 actionable tasks: 1 executed, 7 up-to-date
    ```

### Kapt ✅
1) `git clean -xdf`
2) `./gradlew feature-kotlin:assemble --no-build-cache`
    ```
    w: warning: Running ISOLATING processor on [feature.kotlin.AnnotatedWithIsolating]
    w: warning: Running AGGREGATING processor on [feature.kotlin.AnnotatedWithAggregating]
    w: warning: Running ISOLATING processor on []
    w: warning: Running AGGREGATING processor on []
    
    BUILD SUCCESSFUL in 1s
    12 actionable tasks: 12 executed
    ```
3) `printf "package common;\n\npublic class SomeType {\n    private void something() {}\n}\n" > common/src/main/java/common/SomeType.java`
4) `./gradlew feature-kotlin:assemble --no-build-cache`
    ```
    BUILD SUCCESSFUL in 967ms
    12 actionable tasks: 4 executed, 8 up-to-date
    ```

## ABI-incompatible classpath change
Adds a new public class.

### Gradle ✅
1) `git clean -xdf`
2) `./gradlew feature-java:assemble --no-build-cache`
    ```
    warning: Running ISOLATING processor on [feature.java.AnnotatedWithIsolating]
    warning: Running AGGREGATING processor on [feature.java.AnnotatedWithAggregating]
    warning: Running ISOLATING processor on []
    warning: Running AGGREGATING processor on []

    BUILD SUCCESSFUL in 639ms
    8 actionable tasks: 8 executed
    ```
3) `printf "package common;\n\npublic class SomeType2 {\n}\n" > common/src/main/java/common/SomeType2.java`
4) `./gradlew feature-java:assemble --no-build-cache`
    ```
    warning: Running ISOLATING processor on [feature.java.AnnotatedWithIsolating]
    warning: Running AGGREGATING processor on [feature.java.AnnotatedWithAggregating]
    warning: Running ISOLATING processor on []
    warning: Running AGGREGATING processor on []

    BUILD SUCCESSFUL in 595ms
    8 actionable tasks: 2 executed, 6 up-to-date
    ```
    
### Kapt ❌
1) `git clean -xdf`
2) `./gradlew feature-kotlin:assemble --no-build-cache`
    ```
    w: warning: Running ISOLATING processor on [feature.kotlin.AnnotatedWithIsolating]
    w: warning: Running AGGREGATING processor on [feature.kotlin.AnnotatedWithAggregating]
    w: warning: Running ISOLATING processor on []
    w: warning: Running AGGREGATING processor on []

    BUILD SUCCESSFUL in 1s
    12 actionable tasks: 12 executed
    ```
3) `printf "package common;\n\npublic class SomeType2 {\n}\n" > common/src/main/java/common/SomeType2.java`
4) `./gradlew feature-kotlin:assemble --no-build-cache`
    ```
    BUILD SUCCESSFUL in 1s
    12 actionable tasks: 5 executed, 7 up-to-date
    ```
