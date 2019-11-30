# Flaxo
[![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo)
[![Build Status](https://travis-ci.org/tcibinan/flaxo.svg?branch=dev)](https://travis-ci.org/tcibinan/flaxo)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5b599e5082814d26b34c778670c9985c)](https://www.codacy.com/app/NameOfTheLaw/flaxo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tcibinan/flaxo&amp;utm_campaign=Badge_Grade)
[![Documentation Status](https://readthedocs.org/projects/flaxo/badge/?version=latest)](https://flaxo.readthedocs.io/en/latest/?badge=latest)
![Dev Deploy Status](https://img.shields.io/github/workflow/status/tcibinan/flaxo/Build%20and%20deploy%20flaxo%20release%20to%20dev%20server?label=dev%20deploy)
![Flaxo latest pre-release](https://img.shields.io/github/release-pre/tcibinan/flaxo.svg?label=pre-release)

Flaxo educational platform is a pragmatic way to organise, manage and report programming courses.

## Basics

The educational process using Flaxo platform can be described in five steps:

1. Tutor creates a course which is basically a simple Git repository.
2. Tutor fills the course tasks with tests that students are going to write implementations for.
3. Students solve the tasks and create pull requests.
4. Flaxo evaluates students solutions using several metrics.
5. Tutor receives an interactive dashboard with the students progress.

![course-task-statistics](https://github.com/tcibinan/flaxo/raw/dev/screenshots/course-task.png)

## Features

### Validations

Flaxo uses several integrated services including *Travis*, *Codacy* and *MOSS* to collect different metrics for
all the solutions. It includes **testing**, **code quality analysis** and **plagiarism analysis**.

### Plagiarism

All the solutions for a single course can be analysed for plagiarism.
Plagiarism analysis results can be viewed in a graph viewer tool and their minified representation is 
listed in the course dashboard.

### Generation

Flaxo can generate course\repositories with a preconfigured environment for several languages and testing frameworks.
F.e. gradle project with all the required dependencies can be generated for *java* language and *junit* testing 
framework.

### Export

Course dashboard can be exported in one of the supported formats: json, csv and tsv. 
It makes all kinds of integrations with the Flaxo possible.

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
in the corresponding documentation [page](https://flaxo.readthedocs.io/en/latest/contribution/).

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

It will be great if you add Flaxo badge
[![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo) 
to your course README.md.

```markdown
[![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo)
```
