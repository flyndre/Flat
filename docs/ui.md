# User Interface

Here, the approximate screens for both frontends are documented.
These are mockups, so actual positioning, colors and spacing may differ.

## View Flow

```mermaid
%%{init: {"flowchart": {"htmlLabels": false}} }%%
flowchart LR
  style NotFoundView stroke-dasharray: 5 5
  NotFoundView --> HomeView;

  style / stroke-dasharray: 5 5
  / --> HomeView;

  HomeView <-- settings --> SettingsView;
  HomeView <-- join --> JoinView;
  HomeView <-- presets ---> PresetsView;

  JoinView -- join (participant) --> TrackingView;
  TrackingView -- leave (participant) --> HomeView;

  PresetsView <-- create --> EditView;
  PresetsView <-- edit ---> EditView;

  EditView <-- edit map --> MapView;
  EditView -- start (admin) --> TrackingView;
  TrackingView -- stop (admin) --> PresetsView;
```

## Start Screen

![](/docs/assets/ui/start.drawio.png)

## Collection Management

![](/docs/assets/ui/presets.drawio.png)

## Collection Form

![](/docs/assets/ui/create.drawio.png)

## Join Collection

![](/docs/assets/ui/join.drawio.png)

## Tracking

![](/docs/assets/ui/tracking.drawio.png)

## Participant Management

![](/docs/assets/ui/participants.drawio.png)
