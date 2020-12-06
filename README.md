### todo-or-die

> "todo or not todo?", that is the ultimate question of life
>
> <img src="icon/todo-or-die.png" height="186" alt="todo-or-die"></img>

[![Download](https://api.bintray.com/packages/serpro69/maven/todo-or-die/images/download.svg)](https://bintray.com/serpro69/maven/todo-or-die/_latestVersion)
[![Issues Badge](https://img.shields.io/github/issues/serpro69/todo-or-die.svg)](https://github.com/serpro69/todo-or-die/issues)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![Licence Badge](https://img.shields.io/github/license/serpro69/todo-or-die.svg)](LICENCE.md)

## About

A small **Kotlin Multiplatform** lib that helps you put off coding in a new, cool way, and reminds you to do your TODOs
by throwing errors for your overdue tasks.

<b>Please note that this project is in Alpha version.
 I initially created it to explore Kotlin Multiplatform, particularly publications of multiplatform libraries.
 At the same time the functionality is really primitive and <i>"should work fine"</i> (c) Famous Last Words.</b>
 
### Motivation

Have you ever found yourself in a situation when you develop a piece of software, that it is easier to hack something together to make it work,
knowing very well that you can do a better job, but not taking the extra effort, and justifying it by saying *"I can just fix it later"*? 
Of course, you need to have some kind of reminder of what needs to be done and why (don't even get me started on "remembering things that need to be done"), 
so you add that infamous `#TODO this is a temporary workaround, find a better way to handle this` comment to your code, 
which will be that ultimate reminder for you to fix that "hack".

Or, while working on a piece of code, you spot something else in your codebase that should be fixed and/or improved,
and use the same infamous `#TODO Whoever wrote this piece of cr*p? Oh right, it was me. Refactor this nasty thing ASAP` comment.

Unfortunately, while something along the lines of *"fix it right away"* might sound good on paper,
in reality you can't always do that "fix" right away for various reasons.
Another reality is, as LeBlanc's Law states, `Later == Never`, 
which has been proven time and again by numerous software developers and teams.

## Installation

### Gradle
#### Repositories
The project depends on [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime),
which is currently available only in the following repository:
```kotlin
repositories {
    maven(url = "https://kotlin.bintray.com/kotlinx/")
}
```

[Latest releases](https://bintray.com/serpro69/maven/todo-or-die/_latestVersion) of this lib are always available in jcenter repo:
```kotlin
repositories{
    jcenter()
}
```

#### Dependency
After the above repos are included in your build file, add one of the following to your dependencies:
```kotlin
//JVM
implementation("io.github.serpro69:todo-or-die-jvm:$version")

//JS
implementation("io.github.serpro69:todo-or-die-js:$version")

//MultiPlatform/commonMain
implementation("io.github.serpro69:todo-or-die:$version")
```

## Usage

It's quite simple really. Let's say you want to remind yourself to add some extra tests to your `ControllerTest` class.

```kotlin
class ControllerTest {

    init {
        TODO("2020-12-24") { // The due date after which an error will be thrown
            "Add a test for negative condition" // The description of your task
        }
    }
    
    @Test
    fun testHappyPath() {
        // some test
    }
}
```

The above piece of code will start throwing `OverdueError` from "2020-12-25" onward, 
which is probably a good example only if you want to get some angry calls from your management on the Christmas Day,
but should still give you an overall idea on how to use the functionality.

### *"Won't this explode in my face at the most unexpected moment?"*

Yes, it most definitely will if you forget to finish the task and remove the `TODO`.
By no means am I encouraging you to use this in Production.

At the same time I find it quite useful to have during initial development and in testing,
as well as in my OSS projects.

This also works quite nicely with [TDD](https://en.wikipedia.org/wiki/Test-driven_development)
where you intentionally write crappy code initially just to make it work, and then refactor it later.

Nevertheless, I strongly recommend you to read what the default behavior of this lib does before it leads to you loosing your job.
(Speaking of, please also take note of the lack of any guarantees and warranties in the [licence](LICENCE.md))

### Some failsafe configuration

You can configure your `TODO`s to not blow-up your code, but instead emit safe warnings like so:
```kotlin
TodoOrDieConfig {
    printCantDie = true
    messageLevel = Level.ERROR
}
```

With this, instead of getting those nice `OverdueError`s you will have environmentally-friendly print-outs in your `System.out`.

Just like `Let's eat grandma` can mean two things, same applies to `printCantDie` property.
As they used to say in the good old days: <i>"Punctuation saves lives!"</i>

## Contributing

Feel free to submit a [pull request](https://github.com/serpro69/kotlin-faker/compare) 
and/or open a [new issue](https://github.com/serpro69/kotlin-faker/issues/new)
if you would like to contribute.

## Licence

This code is free to use under the terms of the MIT licence.
See [LICENCE.md](LICENCE.md).

## Thanks

* This project is inspired by [todo_or_die ruby gem](https://github.com/searls/todo_or_die)

* Many thanks to these awesome tools that help us in creating open-source software:  
[![Intellij IDEA](https://cloud.google.com/tools/images/icon_IntelliJIDEA.png)](http://www.jetbrains.com/idea) 
[![YourKit Java profiler](https://www.yourkit.com/images/yklogo.png)](https://www.yourkit.com/features/)
