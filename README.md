# FAF Galactic War backend
This is the server-backend for Galactic War provided by [faforever.com](https://www.faforever.com) (or FAF).

## What is Galactic War?
Galactic War is a meta-strategy game on top of Supreme Commander: Forged Alliance and the FAF services.
It extends the classic FA with a whole galaxy around it, waiting to be conquered.
In a persistent world all four factions fight the infinite war in real-time until only one remains.

More information can be found in the [FAF galactic war forum](http://forums.faforever.com/viewforum.php?f=50&sid=db787242894b54744281ab2c1ea0ce77).


## What is gw-backend?
gw-backend is the server that handles the state of the universe, communicates with gw-clients and the faf lobby server and also offers REST-API services.
A brief description of the overall architecture and used technologies can be found [here](https://www.draw.io/?lightbox=1&highlight=0000ff&layers=1&nav=1&title=GW%20Architecture.xml#Uhttps%3A%2F%2Fdrive.google.com%2Fuc%3Fid%3D0B5Ig45LizpfuYTRWRWl2NmREWXM%26export%3Ddownload).
A more detailled explanation on the architecture around galactic war gives [this video](https://www.youtube.com/watch?v=lUUDdL05QAA)

## What is the current state?
Work has begun, but we are still in a very early stage.

The gw-backend is the spearhead on the Galactic War implementation.
As of now, there is no support in the FAF lobby server and there is also no official client yet.
In the meantime, you can use these tools for development:
- [java developer client](https://github.com/Brutus5000/gw-dev-client)
- [lobby server emulator](https://github.com/Brutus5000/gw-lobby-emulator)
