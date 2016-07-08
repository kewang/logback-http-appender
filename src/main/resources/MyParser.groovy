@Grab('net.sourceforge.nekohtml:nekohtml:1.9.20')

import org.cyberneko.html.parsers.SAXParser

def url = 'https://news.google.com.tw/nwshp?hl=zh-TW&tab=wn'

def parser = new SAXParser()

parser.setFeature('http://xml.org/sax/features/namespaces', false)

def page = new XmlParser(parser).parse(url)

def data = page.depthFirst().DIV.grep {
    it.'@class'=='title'
}

data.each {
    println it.A.SPAN.text()
}