# ILM Interfaces

> This repository is part of the open source project ILM. You can find more information about the project at [ILM](https://github.com/OmniTrustILM/ilm) repository, including the contribution guide.

`Interfaces` is a set of interfaces related to the platform APIs and communication between the platform and available `Connectors`. It contains a description of the interfaces that can be treated as a reference for the developers and integrators.

These interfaces have to be generally applied when extending the platform, in case you would like to develop custom `Connectors` providing some specific functionality or implementing some proprietary protocols.

## Core interfaces

`Core` interfaces are the interfaces that are used by the platform to communicate with the `Connectors` and the clients.

You can find the detailed description of the interfaces in the following sections:
- [Core interfaces](src/main/java/com/otilm/api/interfaces/core)

## Protocol interfaces

`Protocol` interfaces can be used to create a custom implementation and behavior for standard certificate management protocols.

The following interfaces are available:

- [ACME interface](src/main/java/com/otilm/api/interfaces/core/acme/AcmeController.java)
- [RA Profile ACME interface](src/main/java/com/otilm/api/interfaces/core/acme/AcmeRaProfileController.java)

- [SCEP interface](src/main/java/com/otilm/api/interfaces/core/scep/ScepController.java)
- [RA Profile SCEP interface](src/main/java/com/otilm/api/interfaces/core/scep/ScepRaProfileController.java)

- [CMP interface](src/main/java/com/otilm/api/interfaces/core/cmp/CmpController.java)
- [RA Profile CMP interface](src/main/java/com/otilm/api/interfaces/core/cmp/CmpRaProfileController.java)
