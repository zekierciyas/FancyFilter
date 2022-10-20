# Fancy Filter

An Android Library for applying easy, fast and effective 63 (will be added more) LUT filters to photos.


## Initial Installation
### Gradle
Add below codes to your **root** `build.gradle` file (not your module build.gradle file).
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
	        implementation 'com.github.zekierciyas:FancyFilter:Tag'
	}
```


## Usage

The Fancy Filter library, which is fast and easy to use, generally includes a simple Builder Pattern. It is based on providing the necessary parameters and obtaining the filtered image in bitmap type.

```kotlin
 FancyFilter.Builder()
            .withContext(this)
            .filter(FancyFilters.NO_1)
            .bitmap(bitmap)
            .applyFilter { bitmap ->
                // Getting the filtered bitmap here
            }
```



