# Caster

*Work in progress*

Right now it is only possible to stream mp4 files to the Chromecast.

Media server to control ChromeCast.  
Every command can be send via. akka http service.  
The loaded media file is streamed using akka streaming. 

ChromeCast Java API : https://github.com/vitalidze/chromecast-java-api-v2

TODOs: 
 * [ ] Choose between Chromecasts that gets discovered on LAN.
 * [ ] Use encode/decoder library to convert other video standards to MP4 stream.
 * [ ] Use tika or its like to auto detect the media loaded. 
 * [ ] Create another receiver App then the Default Media Receiver.
 * [ ] Clean up code (this will probably never happen)
 * [ ] Not sure we need all that try catch on play/stop/load functions. 
 
 
 ## Useful links (?):
   
 Video Decoder/encoder: https://github.com/artclarke/humble-video  
 More Decoder/encoder: http://www.xuggle.com/xuggler/  
 Even more decoder/encoder: https://github.com/a-schild/jave2
 
 Google cast media support: https://developers.google.com/cast/docs/media  
 More google: https://developers.google.com/cast/v2/receiver_apps  
 Even more google: https://github.com/googlecast
 
 ChromeCast API: https://github.com/vitalidze/chromecast-java-api-v2  
 
 VLCj: https://github.com/caprica/vlcj  
 YouTube about VLCj: https://www.youtube.com/watch?v=XTQ1b3-TUI4  
 VLCj app: https://github.com/ricky739996585/douyu

 Shellpodd VLC cli: https://github.com/PetroSemeniuk/shellpodd/tree/9399d23440f8363775067103b055da762762145b
