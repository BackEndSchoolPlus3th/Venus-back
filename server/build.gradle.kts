plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ll"
version = "0.0.1-SNAPSHOT"
val springCloudVersion by extra("2024.0.0")

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://repo.spring.io/milestone")
	}
}

dependencies {
	//openfeign
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.springframework.boot:spring-boot-starter-security") // spring security
	testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client") // oauth2
	//implementation("org.springframework.data:spring-data-redis") // Redis
	implementation("io.jsonwebtoken:jjwt-api:0.12.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.5") //
    implementation("org.springframework.boot:spring-boot-starter-validation")


	//swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	//implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	// aws s3
	implementation(platform("software.amazon.awssdk:bom:2.24.0"))
	implementation("software.amazon.awssdk:s3")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	// 로그백
	implementation("net.logstash.logback:logstash-logback-encoder:7.4")

	// https://mvnrepository.com/artifact/net.datafaker/datafaker repost 가데이터 밀어넣기
	implementation("net.datafaker:datafaker:2.4.2")

}
dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
	}
}
tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jar {
	enabled = false
}