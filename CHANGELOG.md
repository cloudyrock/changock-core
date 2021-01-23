# 1.0.5
### Features
* [non-fail-fast changeSet](https://github.com/cloudyrock/mongock-core/issues/2)
* [Executed state in changeLog collection](https://github.com/cloudyrock/mongock-core/issues/4)
* [Track failed FAILED and IGNORED changeSet in database](https://github.com/cloudyrock/mongock-core/issues/8)
### Breaking changes
* New builder API: Introduced drivers
* throwExceptionIfCannotObtainLock true by default
### Bugs fixed
* Take older changeSets with no state as executed
__________________________________________________