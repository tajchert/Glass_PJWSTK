Glass PJWSTK app
============

App for Google Glass that show student schedule (got from university server) , at PJWSTK (university in Warsaw).
Simple use of Google Glass GDK.

###Use of an app
Just run it by saying "OK, glass" and "next lesson" as an action name.

###Landing card
In response it will download your schedule for next 7 days, and it will read aloud how many hours are till next lesson (or minutes).
Also a cards will appear, landing card is that one (if there is less then an hour till lesson it will show minutes at the bottom):

![nextLesson](https://raw.githubusercontent.com/tajchert/Glass_PJWSTK/master/screenshots/nextLesson.jpg)

If there is more then one hour till next lesson it will just show the same but without minutes:

![Lesson with minutes](https://raw.githubusercontent.com/tajchert/Glass_PJWSTK/master/screenshots/nextLessonWithMinutes.jpg)

But if you are lucky to be in the middle of a lesson it will show minutes till its end.

![till end](https://raw.githubusercontent.com/tajchert/Glass_PJWSTK/master/screenshots/tillEnd.jpg)


###Card stream
But there is more, apart from landing card (above), it will show rest of your lessons during next 7 days. Each lesson will be represented as a separate card.

![normal lesson](https://raw.githubusercontent.com/tajchert/Glass_PJWSTK/master/screenshots/notNextLesson.jpg)

Also there would be added cards related to days, so lessons from Wednesday and Thursday will be separated with such card.
![weekday](https://raw.githubusercontent.com/tajchert/Glass_PJWSTK/master/screenshots/weekday.jpg)


###Final note
Code ain't beautiful as I borrowed Glasses only for a limited time, and I didn't had time to think it through before coding.
