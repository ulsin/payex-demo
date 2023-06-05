# kjøring

## nettleser
<localhost:8080> for å se frontend

## konsoll
args if any

./mvnw i terminal

## db konsoll

- [ ] check url
- [ ] username and login

# rapporter

## next week
skjønte ikke helt hva som spurtes om her, mange av showene er jo ferdig kjørt, og vil ikke ha noen info om neste uke

## reccomended

skjønte ikke helt denne heller, hva menes?

## Top 10 - Skal liste serier sortert på rating

ez game ez life

## Top Network
Gjorde i kode istedenfor i database, skippet å lage network struktur / implementere den ordentlig

## Summary

ez game ez life
## Top network - Skal liste "network" samt aktuelle tv-serier basert på gjennomsnittlig-rating.

# Liten tid

Hadde liten tid så kunne ikke gjøre alt til standarden jeg ønsker

- ui
- ikke ORM
- valg av database

## Om valg

## Om Samle struktur
ideally så ville jeg ha hatt at shows skulle ha en liste med episoder, men fikk problemer med beanpropertyrowmapper så bestemte meg for å ikke ha det sånn, dog med ORM så ville jeg jo hatt det sånn sånn at koblingen ble lik mellom objekt modellen og tabell strukturen i databasen.

Syntes det er litt dårlig at jeg returnerer et samleobjekt men siden det var limit på API queries og man får alt i samme query så tenkte jeg at det var best å ta alt i en

## Plain UI

virket ikke som at det var fokus på frontend så bare gjorde noe enkelt

## Om ikke ORM
lærte spring boot på en litt dated måte, hadde ikke nok tid til å sette meg inn i den nyere ORM orienterte måten, så ble sånn her denne gangen.

Har jobbet med ORM i .NET så er kjent med konseptet

## Hvorfor H2

Var det vi brukte når jeg var lærer assistent i faget, syntes at postgres er kulere egentlig, men var H2 vi brukte for å raskt bare få en database in memory og også ha en nett consoll, så gikk for det med tanke på tid.

## Om 429 error handling
det skjedde ikke for meg at den error en kom, vet ikke om jeg handlet den feil men fikk hentet alt uten at erroren intraff, handlet for det uansett men fikk ikke testet om den var handlet riktig.