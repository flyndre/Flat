# Conventions

The following conventions are intended to structure and organize collaboration on the project.

## Commits

In order to guarantee a uniform commit history, commits should have the following structure:

`<Activity>: <Summary>`

Possible activities are:

-   **feat:** Feature is added
-   **refactor:** Refactoring of the code
-   **docs:** Documentation of the code
-   **dev:** Changes in the development environment
-   **test:** Changes regarding tests
-   **workflow:** Changes to the CI/CD pipeline

The summary of the activity is in English.

Issues (GitHub tickets) can be referenced using `#<issue number>`.
Using certain keywords like "fixes" or "closes" will automatically close [issues](https://github.blog/2013-01-22-closing-issues-via-commit-messages/).

### Examples

-   fix #263: use correct data type for clientId
-   feat: pwa map display
-   docs: add documentation for android frontend

## Branches

When naming branches, you should also pay attention to a consistent pattern:

`<Subproject>/<Change Type>/<Feature>`

The types specified in commits can be considered as change types.

Here too, the features are described in English.

### Examples

-   frontend-pwa/feature/map-display
-   backend/fix/client-id mapping
-   frontend-android/test/api-test
