ps -ef|grep java|grep hnbank|grep application.yml|awk '{print $2}' | xargs kill -9

nohup java -jar hnbank_screen.jar spl.conf \
   --spring.config.location=config/application.yml > /dev/null &
