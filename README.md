[![CircleCI](https://circleci.com/gh/Ninja-Squad/DbSetup.svg?style=svg)](https://circleci.com/gh/Ninja-Squad/DbSetup)

# Overview

DbSetup allows populating a database before executing automated integration tests (typically, DAO/Repository automated tests). 
Although DBUnit, which is a great project, allows doing the same thing and much more, it's also harder to use and setup. 
And in our experience, in 98% of the cases, DBUnit is only used to pre-populate a database before executing every test method. 
This is the task on which DbSetup concentrates.

The philosophy of DbSetup is that DAO tests should not have to setup the database, execute tests, and then remove everything from the database. 
Instead, a single setup method should be used to delete everything from the database (whatever the previous test put in it, or the initial state 
of the database tables), and then populate it with the data necessary to execute the test.

Another design choice of DbSetup is to provide an easy to use and simple Java API to populate the database, rather than loading data from external 
XML files. Using a Java API has several advantages:

   - It allows using real Java types as data (longs, enums, etc.)
   - It allows defining default values, looping to generate several similar rows, storing data sets in variables or factorizing their 
     creation using resusable methods
   - It allows viewing the data sets easily, without having to open external files, by storing the data set directly into the test class, 
     or by navigating through classes and methods using the IDE shortcuts.
   - For more complex situations, like cyclic referential integrity constraints between rows, the Java API allows easily integrating SQL 
     statements into the sequence of operations to execute to pre-populate the database. These SQL statements can, for example, disable 
     constraints and re-enable them.

# Documentation

The documentation is available at [dbsetup.ninja-squad.com](http://dbsetup.ninja-squad.com). It's hosted by github,
from the gh-pages branch.

# Bugs and RFEs

To submit bugs or RFEs, use [Github](https://github.com/Ninja-Squad/DbSetup/issues).

# How to...
## build

    ./gradlew build

## install the artifacts in your own local Maven repository
    
    ./gradlew publishToMavenLocal
    
## release a new version of DbSetup

 - Make sure you have a file named `gradle.properties` under `HOME/.gradle`, and this file contains the following 
properties:

        sonatypeUsername=<the sonatype user name>
        sonatypePassword=<the sonatype password>       
        signing.keyId=<the ID of the PGP key to use>
        signing.password=<the password used to protect your PGP key configured in the ninjasquad bintray account>
        signing.secretKeyRingFile=<the path to the secring.jpg file>

 - Make sure the version of DbSetup in the project's `gradle.properties` file is the right one. It must not end with `-SNAPSHOT`
 - Execute
 
        ./gradlew clean build publishToSonatype closeAndReleaseRepository
        
That should create a new staging repo in Sonatype OSSRS, then upload the artefacts to this staging repo, 
sign close the repo and release (i.e. sync it to Maven central).

To only close the repo to test without syncing to Maven central, run

    ./gradlew clean build publishToSonatype closeRepository

Note: to check the jar file and pom publication before uploading, you can use 

    ./gradlew publishMavenPublicationToBuildRepository
    
which will simply publish under the `build/repo` directory of the root project.

See the publishing guide at https://github.com/rwinch/gradle-publish-ossrh-sample.

# License

DbSetup is released under the [MIT License](http://en.wikipedia.org/wiki/MIT_License).

The MIT License

Copyright (c) 2012-2013, Ninja Squad

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
