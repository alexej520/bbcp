# BBCP (BBCode Parser)

This is BBCode Parser Library that allow to parse BBCode text as Tree of Elements (Document).

Root Element can contains only children Elements. The Element that contains Tag can have a children Elements and attribute set. Element that contains String cannot have a children Elements.

Since BBCode can have specific Syntax depends Tag name, this library provides the most common Syntax Parsers:
* DefaultSyntaxParser that parse default BBCode syntax
* PlainSyntaxParser that not parse inner tags
* ListSyntaxParser that can parse not closed list item tags as below
```
[LIST]
[*]item 1
[*][B]bold item 2[/B]
[*] item 3
[/LIST]
```

## Usage
1. Create Bb instance
Register SyntaxParser for specific tags (null for root)
If you want to parse all valid tags you can register default SyntaxParser that will try parse any tags, that have not specific SyntaxParser
```java
// specific tags only 
Bb bb = Bb.builder(true)
    .register(new DefaultSyntaxParser(), null, "TAG", "B")
    .build();

// any tags
Bb bb = Bb.builder(true)
    .registerDefault(new DefaultSyntaxParser())
    .build();
```
2. Parse String
```java
Document document = bb.parse("[B]bold[\B]");
```
3. Get Tag and Text
```java
Element element = document.getChilds().get(0);
Tag tag = element.getTag();
tag.getName().equals("B");
element.getChilds().get(0).getText().equals("bold");
```
4. Get Attributes
```java
Document doc = bb.parse("[URL=example.com]example[/URL][TAG key=value]text[/TAG]");
Tag urlTag = doc.getChilds().get(0).getTag();
urlTag.getAttributes().get("URL").equals("example.com")

Tag tag = doc.getChilds().get(1).getTag();
tag.getAttributes().get("key").equals("value")
```

5. More examples you can find in [Tests](src/test/java/ru/lextop/bbcp/)

## Dependencies
Gradle
```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
    compile 'com.github.alexej520:bbcp:1.1'
}
```
Maven
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
	<groupId>com.github.alexej520</groupId>
	<artifactId>bbcp</artifactId>
	<version>1.1</version>
</dependency>
```
