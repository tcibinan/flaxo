# Flaxo
[![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo)
[![Build Status](https://travis-ci.org/tcibinan/flaxo.svg?branch=dev)](https://travis-ci.org/tcibinan/flaxo)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5b599e5082814d26b34c778670c9985c)](https://www.codacy.com/app/NameOfTheLaw/flaxo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tcibinan/flaxo&amp;utm_campaign=Badge_Grade)
[![Docker Cloud Build Status Frontend](https://img.shields.io/docker/cloud/build/flaxo/frontend.svg?label=frontend%20docker)](https://hub.docker.com/r/flaxo/frontend)
[![Docker Cloud Build Status Backend](https://img.shields.io/docker/cloud/build/flaxo/backend.svg?label=backend%20docker)](https://hub.docker.com/r/flaxo/backend)
[![Documentation Status](https://readthedocs.org/projects/flaxo/badge/?version=latest)](https://flaxo.readthedocs.io/en/latest/?badge=latest)

Flaxo educational platform is a pragmatic way to organise, manage and report programming courses.

## Basics

The educational process using flaxo platform can be described in five steps:

1. Tutor creates a course with flaxo which is basically a simple git repository.
2. Tutor fill the course tasks with tests that students are going to write implementations for.
3. Students solve the tasks and create pull requests.
4. Flaxo make all the necessary arrangements to evaluate students solutions.
5. Tutor receives well-formatted statistics of students progress.

## Features

### Results aggregation

Flaxo aggregates results for each course task using different metrics including: 

- solutions testing
- code style analysis
- plagiarism analysis

![course-task-statistics](https://github.com/tcibinan/flaxo/raw/dev/screenshots/course-task.png)

### Course generation

You can create a course from scratch just selecting languages and framework for testing.
Flaxo knows how to create a git repository with a required gradle project.

![course-creation-modal](https://github.com/tcibinan/flaxo/raw/dev/screenshots/course-creation-modal.png)

### Statistics export

All courses statistics could be retrieved in one of the supported formats: json, csv and tsv.

### Educational flow

You can create unlimited amount of public courses for free. And it is as easy as it can be.

![all-courses](https://github.com/tcibinan/flaxo/raw/dev/screenshots/all-courses.png)

## Course examples

[Data structures on Java](https://github.com/tcibinan/data-structures-course) :ru:

## Getting started

To get your flaxo instance up and running you can follow the [Getting started wiki page](https://github.com/tcibinan/flaxo/wiki/Getting-started).

## Contributing

Please feel free to open issues and ask questions. Contribution to Flaxo is more than welcome.
Information on how to build flaxo from sources can be found in the corresponding 
[Build from sources wiki page](https://github.com/tcibinan/flaxo/wiki/Build-from-sources).

## What's inside

### Integrated external services

- Github
- Travis CI
- Codacy
- [Moss](https://theory.stanford.edu/~aiken/moss/)

### Back-end technologies

- Kotlin
- Gradle
- Spring
- Spek

### Front-end technologies

- Kotlin
- Webpack
- React
- Bootstrap

## Credits

```json
{ 
  "full_name": "Andrey Tsibin",
  "email": "tsibin.andr@gmail.com",
  "telegram": "@Nameofthelaw",
  "vk": "https://vk.com/id24276156",
  "app_icon": "by Anton Ivanov from the Noun Project",
  "year": 2018,
  "city": "Saint-Petersburg"
}
```

It will be great if you add flaxo badge [![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo) to your course README.md.

```markdown
[![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo)
```
