# Fancy Filter

An Android Library for applying easy, fast and effective 62 (will be added more) LUT filters to photos. With RenderScript, it is aimed to process the color spaces in the LUT and apply them to the picture in the form of a filter.

## What is LUT?
The direct translation of LUT ("Lookup Table") from English means "lookup table". In computer science, it means data where input (input) values and output (output) values are mapped and calculated. 

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

# License
```xml
Copyright 2022 github/zekierciyas (Zeki Erciyas)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```


