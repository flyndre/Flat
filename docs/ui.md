# User Interface

Here, the approximate screens for both frontends are documented.
These mockups are meant to transport the basic ideas of the ui.
Actual positioning, colors and spacing may differ.

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

  EditView <-- edit map --> DivisionsView;
  EditView -- start (admin) --> TrackingView;
  TrackingView -- stop (admin) --> PresetsView;
  TrackingView <-- manage (admin) --> ParticipantsView;
```

## Home View

![](/docs/assets/ui/start.drawio.png)

## Presets View

![](/docs/assets/ui/presets.drawio.png)

## Create/Edit Collection View

![](/docs/assets/ui/create.drawio.png)

## Create/Edit Collection Divisions View

```js
availableColors = ["#1E90FF", "#FF1493", "#32CD32", "#FF8C00", "#4B0082"];
```

![](/docs/assets//ui/divisions.drawio.png)

## Join View

![](/docs/assets/ui/join.drawio.png)

## Tracking View

![](/docs/assets/ui/tracking.drawio.png)

## Participant Management View

![](/docs/assets/ui/participants.drawio.png)
