# Zipties - for MC 1.12.2

I'm working on completing the Zipties plugin made by [Mishyy](https://github.com/Mishyy/Zipties)\
*(Mitch wrote the large majority of the code, I simply finished what he started.)*\
A pre-compiled build is available to you on the releases tab, or you can build it yourself with gradle.

## Crafting!
Can be turned off or on in config.yml

![Crafting Recipes](https://i.imgur.com/HoOwr1o.png "Crafting recipes")
## Default Configuration:
```yaml
messages:
  prefix: '&2&lZipties &8»'
  commands:
    zipties: '%prefix% &7You were given zipties.'
    cutters: '%prefix% &7You were given cutters.'
    player: '%prefix% &cCould not find player matching &7%name%&c.'
    permission: '%prefix% &cYou do not have permission.'
    help: |-
      %prefix% &7Zipties v&a${version}
      %prefix% &a/%cmd% [help] &7Displays this help message.
      %prefix% &a/%cmd% release [player] &7Releases the player from the restraints you put on them.
  messages:
    cooldown: '%prefix% &7You must wait &a%cooldown% &7more seconds before your next attempt!'
    escapefail: '%prefix% &7Your escape attempt failed'
    closer: '%prefix% &7You are not close enough to the detainee!'
    restrainfail: '%prefix% &7Restraint failed!'
    inprogress: '%prefix% &7You begin to attempt tying &a%prisoner%&7!'
  restrained:
    already: '%prefix% &cThat player is already restrained!'
    self: '%prefix% &7You have restrained &a%prisoner%&7!'
    other: '%prefix% &7You have been restrained by &a%restrainer%&7!'
    isalready: '%prefix% &cYou are restrained, so cannot restrain others!'
    left: '%prefix% &7You disconnected while restrained, so you died!'
  released:
    restrainer: '%prefix% &7You have released &a%prisoner%&7!'
    prisoner: '%prefix% &7You have been released!'
  escaped:
    restrainer: '%prefix% &a%prisoner% &7has broken out of your restrained!'
    prisoner: '%prefix% &7You have broken out of your restraints!'
    almost: '%prefix% &a%progress% &7of the restraints are broken!'
    free: '%prefix% &7You have broken &a%prisoner% &7free!'

items:
  zipties:
    type: 'GOLD_HOE'
    data: 22
    name: '&eZipties'
  cutters:
    type: 'SHEARS'
    data: 0
    name: '&eZiptie Cutters'

cutters:
  # How many clicks do cutters need to remove zipties?
  count: 10

# Allow crafting of zipties & cutters?
crafting: true
# Time, in ticks, it takes to tie someone with zipties.
# 20 = 1 second (at 20TPS)
tieTime: 20
```
## Permissions
* zipties.use - use zipties
* zipties.bypass - blocks you from getting ziptied
* zipties.admin - allows you to use /zipties release <player>, /zipties zipties, and /zipties cutters

### Other things you should know
* Everybody can release others using cutters.
* Everybody can escape zipties by tapping SNEAK button while tied up. (It's a very small chance of success though)
* Prisoners are given Weakness when they're tied and removed of weakness when they are released.