#! /bin/bash
java --class-path /home/pfotenhauer/git/SVWS-Server/svws-db-utils/build/classes/java/main:/home/pfotenhauer/git/SVWS-Server/svws-db-utils/build/resources/main:/home/pfotenhauer/git/SVWS-Server/svws-db-dto/build/libs/svws-db-dto-0.6.6-SNAPSHOT.jar:/home/pfotenhauer/git/SVWS-Server/svws-db/build/libs/svws-db-0.6.6-SNAPSHOT.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/de.nrw.schule.svws.ext.jbcrypt/jbcrypt/0.4.0/d0adc192a73e7e6c7b61681f4d5ef4455c64de83/jbcrypt-0.4.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.eclipse.persistence/org.eclipse.persistence.jpa/4.0.0/ebc5e34be212c408b84d69d841974ebd22f68e7d/org.eclipse.persistence.jpa-4.0.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/jakarta.persistence/jakarta.persistence-api/3.1.0/66901fa1c373c6aff65c13791cc11da72060a8d6/jakarta.persistence-api-3.1.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.eclipse.persistence/jakarta.persistence/2.2.3/ccb72277523e79f81bbf74535bf6488443403bae/jakarta.persistence-2.2.3.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.mariadb.jdbc/mariadb-java-client/3.1.0/4510549f3fe01b8ab634186f5c314f663d91eada/mariadb-java-client-3.1.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.xerial/sqlite-jdbc/3.40.0.0/a2637f9aaa89668ffdbf62314072d1a345a8a71b/sqlite-jdbc-3.40.0.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/net.sf.ucanaccess/ucanaccess/5.0.1/1a362db0e8f4bfa727b033f20c52542ea6416259/ucanaccess-5.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.healthmarketscience.jackcess/jackcess/4.0.4/8c1686eb5d8438b35bba6ba1ddf8aeb898deb918/jackcess-4.0.4.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.microsoft.sqlserver/mssql-jdbc/9.4.1.jre16/c07577972d1d9c37df05f11dcc050a14cee79fe1/mssql-jdbc-9.4.1.jre16.jar:/home/pfotenhauer/git/SVWS-Server/svws-core/build/libs/svws-core-0.6.6-SNAPSHOT.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.dataformat/jackson-dataformat-xml/2.14.1/ccd98bd674080338a6ca4bcdd52be7fb465cec1d/jackson-dataformat-xml-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.jboss.resteasy/resteasy-jackson2-provider/6.2.1.Final/c512730e1af5f4547271ce88f582b481ee3f7588/resteasy-jackson2-provider-6.2.1.Final.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/io.swagger.core.v3/swagger-jaxrs2-jakarta/2.2.7/12d3c78cc103d2a2a0ae9cd59c88dcd9acf910da/swagger-jaxrs2-jakarta-2.2.7.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.jakarta.rs/jackson-jakarta-rs-json-provider/2.14.1/76f54e1193db270157a11fe4b18a1300985b8d0b/jackson-jakarta-rs-json-provider-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.module/jackson-module-jakarta-xmlbind-annotations/2.14.1/dd91ea111e70c394619feb251dc2e0a70a634a7d/jackson-module-jakarta-xmlbind-annotations-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.jakarta.rs/jackson-jakarta-rs-base/2.14.1/1d7b39834dfe5b46536711e9b4dbb8f53d99455f/jackson-jakarta-rs-base-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/io.swagger.core.v3/swagger-integration-jakarta/2.2.7/daad69fa8f4d0efb8a4a6e87f7bd6655dcd9b99d/swagger-integration-jakarta-2.2.7.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/io.swagger.core.v3/swagger-core-jakarta/2.2.7/9111d85fc116ad6ec9d5f6f54edf81c2740e52/swagger-core-jakarta-2.2.7.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml/2.14.1/cf6d18651659a2e64301452c841e6daa62e77bf6/jackson-dataformat-yaml-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.datatype/jackson-datatype-jsr310/2.14.1/f24e8cb1437e05149b7a3049ebd6700f42e664b1/jackson-datatype-jsr310-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.14.1/7a07bc535ccf0b7f6929c4d0f2ab9b294ef7c4a3/jackson-core-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.dataformat/jackson-dataformat-csv/2.14.1/ff08d648dab89f8a28ae9db3ae8ce7bfd7b25afd/jackson-dataformat-csv-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.github.java-json-tools/json-patch/1.13/c8b72249e50fe778e7df223e5b1fed1931a4a688/json-patch-1.13.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.github.java-json-tools/jackson-coreutils/2.0/6374371261b91b829d10f21256b2feefdf8f0a78/jackson-coreutils-2.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.14.1/268524b9056cae1211b9f1f52560ef19347f4d17/jackson-databind-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/io.swagger.core.v3/swagger-models-jakarta/2.2.7/b26a24f51b009fe77a0b4b4f67738a60aaff1aa2/swagger-models-jakarta-2.2.7.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.14.1/2a6ad504d591a7903ffdec76b5b7252819a2d162/jackson-annotations-2.14.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.jboss.resteasy/resteasy-multipart-provider/6.2.1.Final/3182bb92cb3196a0bfcb272d60ab908aa0de5a70/resteasy-multipart-provider-6.2.1.Final.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.jboss.resteasy/resteasy-core/6.2.1.Final/3ab5a00de6537ed1f99154515989e2fffbd97a85/resteasy-core-6.2.1.Final.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.jboss.resteasy/resteasy-jaxb-provider/6.2.1.Final/69359f76c3f750183cf4fcb2c16ce6cb1021ea7d/resteasy-jaxb-provider-6.2.1.Final.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/io.swagger.core.v3/swagger-annotations-jakarta/2.2.7/1629437bff242e019f5fbac9b27dddd6df47a23b/swagger-annotations-jakarta-2.2.7.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.webjars.npm/swagger-ui-dist/4.15.5/eccadfa534678429cf33de0210eb57b7b3c8d0b7/swagger-ui-dist-4.15.5.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.eclipse.persistence/org.eclipse.persistence.asm/9.4.0/beecc6436a613956dbc9caeed144c8cd50a9eb9c/org.eclipse.persistence.asm-9.4.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.eclipse.persistence/org.eclipse.persistence.core/4.0.0/277c25d7fc0780c3bab984a8066c8c9a709668ae/org.eclipse.persistence.core-4.0.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.eclipse.persistence/org.eclipse.persistence.jpa.jpql/4.0.0/4c4429da8a4b448fd45d028237cc30d60c052528/org.eclipse.persistence.jpa.jpql-4.0.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.github.waffle/waffle-jna/3.2.0/6ac5882dea3a15575237ef5d1793c51279ae1335/waffle-jna-3.2.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.hsqldb/hsqldb/2.5.0/59298fcd77faf01e02b405def2f80cccbf582508/hsqldb-2.5.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-lang3/3.12.0/c6842c86792ff03b9f1d1fe2aab8dc23aa6c6f0e/commons-lang3-3.12.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/commons-logging/commons-logging/1.2/4bfc12adfe4842bf07b657f0369c4cb522955686/commons-logging-1.2.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.mysql/mysql-connector-j/8.0.31/3fd5850719d7e82d50705d34cc6a0037fab5731f/mysql-connector-j-8.0.31.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.jboss.resteasy/resteasy-core-spi/6.2.1.Final/ca770da100d2ae8a617818c7e06e65169a32b541/resteasy-core-spi-6.2.1.Final.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/jakarta.validation/jakarta.validation-api/3.0.2/92b6631659ba35ca09e44874d3eb936edfeee532/jakarta.validation-api-3.0.2.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.fasterxml.woodstox/woodstox-core/6.4.0/c47579857bbf12c85499f431d4ecf27d77976b7c/woodstox-core-6.4.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.codehaus.woodstox/stax2-api/4.2.1/a3f7325c52240418c2ba257b103c3c550e140c83/stax2-api-4.2.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/jakarta.xml.bind/jakarta.xml.bind-api/3.0.1/5257932df36ff3e4e6de50429dde946490a6a800/jakarta.xml.bind-api-3.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/jakarta.mail/jakarta.mail-api/2.1.0/62da0425eb4f2c0146d153e64a9f4f0447985c69/jakarta.mail-api-2.1.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/jakarta.activation/jakarta.activation-api/2.1.0/a58861b5deac5e151140511cf57d6b80a83f2d20/jakarta.activation-api-2.1.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.jboss.logging/jboss-logging/3.5.0.Final/c19307cc11f28f5e2679347e633a3294d865334d/jboss-logging-3.5.0.Final.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/jakarta.annotation/jakarta.annotation-api/2.1.1/48b9bda22b091b1f48b13af03fe36db3be6e1ae3/jakarta.annotation-api-2.1.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/jakarta.ws.rs/jakarta.ws.rs-api/3.1.0/15ce10d249a38865b58fc39521f10f29ab0e3363/jakarta.ws.rs-api-3.1.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.jboss/jandex/2.4.3.Final/e12b6a3f2ebc34ad84eedd735f378087205a39ee/jandex-2.4.3.Final.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.reactivestreams/reactive-streams/1.0.4/3864a1320d97d7b045f729a326e1e077661f31b7/reactive-streams-1.0.4.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.eclipse.angus/angus-activation/1.0.0/f0ceddd49f92109fbfad9125e958f5bfd3f2aa1/angus-activation-1.0.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.ibm.async/asyncutil/0.1.0/440941c382166029a299602e6c9ff5abde1b5143/asyncutil-0.1.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.eclipse.angus/angus-mail/1.0.0/fa0a22df6dae0a1f081ba56a60704627b86d1f22/angus-mail-1.0.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.apache.james/apache-mime4j-storage/0.8.7/dd3275b1205ef7c3ba7f8214f93a504921bc9c68/apache-mime4j-storage-0.8.7.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.apache.james/apache-mime4j-dom/0.8.7/b62a21c1fa8be6d67bccbc7de93f0fe67ccc6b15/apache-mime4j-dom-0.8.7.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/commons-io/commons-io/2.11.0/a2503f302b11ebde7ebc3df41daebe0e4eea3689/commons-io-2.11.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/codemodel/4.0.1/6b4f0758275a4f269fc3ef3aa60236095090840b/codemodel-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/jaxb-core/4.0.1/b4707bb31dfcf54ae424b930741f0cd62d672af9/jaxb-core-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/jaxb-jxc/4.0.1/aa96131da4fe806c0f3c5a11a2888e5dc3248ae0/jaxb-jxc-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/jaxb-runtime/4.0.1/7abfa1ee788a8f090dc598c45876ef068731e72b/jaxb-runtime-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/txw2/4.0.1/797720dfe2e15504f6014fb82eb873051a653c75/txw2-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/jaxb-xjc/4.0.1/de778abe18b6e95e3520f7e528651219e2df9de7/jaxb-xjc-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/xsom/4.0.1/c5090eb5be13b371030fd61eb33c82e2d22f3021/xsom-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.sun.istack/istack-commons-runtime/4.1.1/9b3769c76235bc283b060da4fae2318c6d53f07e/istack-commons-runtime-4.1.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.sun.istack/istack-commons-tools/4.1.1/c54b869d6f419c6a2f4a464bd7931156bf4ab1b9/istack-commons-tools-4.1.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.sun.xml.bind.external/relaxng-datatype/4.0.1/e244fe7da374c28f2bb545110527c46d15cfad04/relaxng-datatype-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.sun.xml.bind.external/rngom/4.0.1/2ebd034c17dab80fe7e077cf4743f9191146284c/rngom-4.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/io.github.classgraph/classgraph/4.8.150/bc3905363daa286949572cde4cb823e1b1f9be93/classgraph-4.8.150.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.javassist/javassist/3.25.0-GA/442dc1f9fd520130bd18da938622f4f9b2e5fba3/javassist-3.25.0-GA.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.yaml/snakeyaml/1.33/2cd0a87ff7df953f810c344bdf2fe3340b954c69/snakeyaml-1.33.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/net.java.dev.jna/jna-platform/5.12.1/97406a297c852f4a41e688a176ec675f72e8329/jna-platform-5.12.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/net.java.dev.jna/jna/5.12.1/b1e93a735caea94f503e95e6fe79bf9cdc1e985d/jna-5.12.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.slf4j/jcl-over-slf4j/1.7.36/d877e195a05aca4a2f1ad2ff14bfec1393af4b5e/jcl-over-slf4j-1.7.36.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.36/6c62681a2f655b49963a5983b8b0950a6120ae14/slf4j-api-1.7.36.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.github.ben-manes.caffeine/caffeine/2.9.3/b162491f768824d21487551873f9b3b374a7fe19/caffeine-2.9.3.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.checkerframework/checker-qual/3.23.0/2ce274da87ae21d940ded7b827d9069206ea3001/checker-qual-3.23.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.google.protobuf/protobuf-java/3.19.4/748e4e0b9e4fa6b9b1fe65690aa04a9db56cfc4d/protobuf-java-3.19.4.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.sun.activation/jakarta.activation/2.0.1/828b80e886a52bb09fe41ff410b10b342f533ce1/jakarta.activation-2.0.1.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.github.java-json-tools/msg-simple/1.2/a06afa2d5d75c98e54ab370107930978fc3f9937/msg-simple-1.2.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/org.apache.james/apache-mime4j-core/0.8.7/d34b9fe324100387cac84100336daa08c32aa16/apache-mime4j-core-0.8.7.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.google.errorprone/error_prone_annotations/2.10.0/9bc20b94d3ac42489cf6ce1e42509c86f6f861a1/error_prone_annotations-2.10.0.jar:/home/pfotenhauer/.gradle/caches/modules-2/files-2.1/com.github.java-json-tools/btf/1.3/6cf5405e214cbc83337a107cdef8401fb6aa6383/btf-1.3.jar de.svws_nrw.db.utils.app.CreateSchema "$@"
