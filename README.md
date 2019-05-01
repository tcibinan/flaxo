# Flaxo
[![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo)
[![Build Status](https://travis-ci.org/tcibinan/flaxo.svg?branch=dev)](https://travis-ci.org/tcibinan/flaxo)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5b599e5082814d26b34c778670c9985c)](https://www.codacy.com/app/NameOfTheLaw/flaxo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tcibinan/flaxo&amp;utm_campaign=Badge_Grade)
[![Docker Cloud Build Status Frontend](https://img.shields.io/docker/cloud/build/flaxo/frontend.svg?label=frontend%20docker)](https://hub.docker.com/r/flaxo/frontend)
[![Docker Cloud Build Status Backend](https://img.shields.io/docker/cloud/build/flaxo/backend.svg?label=backend%20docker)](https://hub.docker.com/r/flaxo/backend)
[![Documentation Status](https://readthedocs.org/projects/flaxo/badge/?version=latest)](https://flaxo.readthedocs.io/en/latest/?badge=latest)
![Flaxo latest pre-release](https://img.shields.io/github/release-pre/tcibinan/flaxo.svg?label=pre-release)

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

## Examples

Several University courses and a few developer trainings were hosted using Flaxo.
Some of them are listed below.

- [Data structures on Java](https://github.com/tcibinan/data-structures-course) `Russian`
- [Java basics](https://github.com/thejerome/IFMO_JAVA_Basics_20182009) `Russian`
- [C++ basics](https://github.com/thejerome/IFMO_CPP_programming_20180910) `Russian`

## Documentation

Flaxo documentation is still in progress. 
Nevertheless some useful information on how to deploy your Flaxo instance and get started is already present.
Documentation can be found [here](https://flaxo.readthedocs.io/en/latest/).

## Contribution

Flaxo is entirely an opensource project and only the community can make a difference. 
So please feel free to open new issues asking questions, suggesting new features or reporting bugs.

If you intend to contribute to Flaxo project then you can found information on how to build Flaxo from sources 
in the corresponding [documentation page](https://flaxo.readthedocs.io/en/latest/contribution/).

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
