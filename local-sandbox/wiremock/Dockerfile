FROM rodolpheche/wiremock

COPY wiremock-body-transformer-1.1.8-SNAPSHOT.jar /var/wiremock/extensions/
COPY __files /home/wiremock/__files
COPY mappings /home/wiremock/mappings

CMD ["--extensions", "com.opentable.extension.ThymeleafBodyTransformer"]