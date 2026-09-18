# Animal Weights Scaling

A [NeoForge](https://neoforged.net/) 1.21.1 addon for [Animal Weights](https://github.com/joseph-osullivan/animal-weights). It scales farm animals from their weight value so underfed animals look emaciated and well-fed animals look plump.

Width (X/Z) changes more than height. Hitboxes stay vanilla so well-fed animals do not get stuck in 1-block gaps.

## Requirements

- Minecraft **1.21.1**
- NeoForge **21.1.x**
- **Animal Weights** (`animalweights`) — required on client and server

## Scale table

Animal Weights stores an integer weight from 0 to 8. This addon maps that value to:

| Weight | Width | Height | Look |
|--------|-------|--------|------|
| 0 | 0.78× | 0.94× | Visibly emaciated |
| 1 | 0.86× | 0.97× | Lean |
| 2 | 0.91× | 0.98× | |
| 3 | 0.96× | 0.99× | |
| 4 | 1.00× | 1.00× | Average |
| 5 | 1.05× | 1.01× | |
| 6 | 1.10× | 1.02× | |
| 7 | 1.15× | 1.03× | |
| 8 | 1.20× | 1.04× | Well-fed / plump |

Weight is read from Animal Weights via `WeightAttachment.getWeight(animal)` when that API is present. If the parent mod uses `AnimalWeightAttachment` instead, this addon falls back to that helper, then to the `animalweights:weight` attachment.

## Install

1. Install NeoForge 1.21.1.
2. Put **Animal Weights** and this mod's jar in the `mods` folder.
3. Launch the game.

Both mods must be installed on the client and the server.

Build output: `build/libs/animalweightsscaling-1.0.0.jar`

## Build from source

This project uses Java 21 and the NeoForge MDK.

```bat
gradlew.bat build
```

On Unix:

```sh
./gradlew build
```

If you have an Animal Weights jar for compile-time checks, place it at `libs/animalweights.jar`. It is not required to build; the parent mod is still required at runtime.

## License

MIT
