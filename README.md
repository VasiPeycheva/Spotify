## Final project for Modern Java Technology course. 

----------------------------------

# Spotify :notes:

[`Spotify`](https://www.spotify.com/) е платформа за `stream`-ване на музика, която предоставя на потребителите достъп до милиони песни на изпълнители от цял свят.

> `Stream`-ването е метод за предаване на данни, използван обикновено за мултимедийни файлове. При него възпроизвеждането на съдържанието върху устройството на потребителя започва още с достъпването му, без да се налага то отначало да бъде изтеглено изцяло като файл и после да се стартира в подходящ плеър. Предаването на данните протича едновременно с възпроизвеждането, затова е необходима постоянна мрежова свързаност.

## Условие

Създайте приложение по подобие на `Spotify`, състоящо се от две части - сървър и клиент.

### **Spotify Server**

Предоставя следните функционалности на клиента:
- регистриране в платформата чрез **email** и **парола** (**потребителите трябва да се съхраняват във файл**)
- login в платформата чрез **email** и **парола**
- съхраняване на набор от песни, достъпни на потребителите за слушане
- търсене на песни
- създаване на статистика на най-слушаните песни от потребителите
- създаване на плейлисти (**плейлистите трябва да се съхраняват във файлове**)
- добавяне на песни към плейлисти
- връщане на информация за даден плейлист
- `stream`-ване на песни

### **Spotify Client**

`Spotify` клиентът трябва да има `command line interface` със следните команди:

```bash
register <email> <password>
login <email> <password>
search <words> - връща всички песни, в чиито имена или имената на изпълнителите iм, се среща потърсената дума (или думи)
top <number> - връща списък с *number* от най-слушаните песни в момента, сортиран в намаляващ ред
create-playlist <name_of_the_playlist>
add-song_to <name_of_the_playlist>
show-playlist <name_of_the_playlist>
play <song>
stop
```

## Забележки:

1. За да можете да изпълнявате песни от `Spotify` клиента, използвайте API-то `javax.sound.sampled`.
2. `javax.sound.sampled` работи само с файлове във [wav](https://en.wikipedia.org/wiki/WAV) формат, затова всички песни, които имате на сървъра, трябва да са **.wav**
3. `javax.sound.sampled` предоставя два начина за възпроизвеждане на музика - чрез `Clip` и `SourceDataLine`. `Clip` се използва когато имаме `non-real-time` музикални данни (файл), които могат да бъдат предварително заредени в паметта.
`SourceDataLine` се използва за `stream`-ване на данни, като например голям музикален файл, който не може да се зареди в паметта наведнъж, или за данни, които предварително не са известни. (за повече информация [тук](https://docs.oracle.com/javase/tutorial/sound/playing.html))

    За целите на проекта, трябва да използвате `SourceDataLine`.
	1. За да създадем [`SourceDataLine`](https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/SourceDataLine.html) първо трябва да знаем конкретния формат на данните, които ще получаваме по мрежата. Това става с класа [`AudioFormat`](https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/AudioFormat.html). За да успеем да възпроизведем дадена песен при клиента, трябва предварително да знаем какъв е този формат.
	
	2. Преди сървърът да започне да ни `stream`-ва  песента, той трябва да ни даде(прати) информация за формата на данните. Класът `AudioFormat` не е `Serializable`, т.е не можем да го прехвърляме по мрежата.
	
	3. За да вземем формата на песента на сървъра, можем да използваме следния код:
        ```java
        AudioFormat audioFormat = AudioSystem.getAudioInputStream(new File(song)).getFormat();
        ```
	
	4. Данните, които са необходими на клиента, са всички полета от конструктора на `AudioFormat`. Те могат да бъдат достъпени чрез съответните `getter` методи:
        ```java
        AudioFormat(AudioFormat.Encoding encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize, float frameRate, boolean bigEndian)
        ```
	
	5. След като сървърът е изпратил формата на данните, клиентът вече е готов да създаде съответния `SourceDataLine` обект, чрез който ще се възпроизвежда песента.
        ```java
        Encoding encoding = ...;
        int sampleRate = ...;
        ...
        AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
        dataLine.open();
        dataLine.start(); // Имайте предвид, че SourceDataLine.start() пуска нова нишка. За повече информация, може да проверите имплементацията.
        ```
    6. За да запишем данни в `SourceDataLine` обекта (данните, които искаме да възпроизведем) използваме следния метод:
	    ```java
	    dataLine.write(byte[] b, int off, int len);
	    ```
    
    7. За тестови цели, можем да си пуснем песен (non-real-time) със следния код:
    
        ```java
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File("<music>.wav"));
        SourceDataLine dataLine = AudioSystem.getSourceDataLine(stream.getFormat());
        dataLine.open();
        dataLine.start();
        
        while (true);
        ```
  4. Валидирайте по подходящ начин командите
  5. При възникване на програмна грешка, извеждайте в конзолата смислени съобщения на потребителя - не се предполага клиентът да има умения да `troubleshoot`-ва `exception`-и, затова `printStackTrace()` не е добър вариант. Хврълените `exception`-и записвайте във файл.

