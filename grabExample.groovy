@Grapes( @Grab('org.jsoup:jsoup:1.10.2'))
import org.jsoup.Jsoup
import java.io.File

class Fetch {

def getPageLinks01(domain,url)
{
	println "fetching URL "+domain+url

	def doc = Jsoup.connect(domain+url).get()
	totalList.add(url)
	for(element in doc.select("a"))
	{
		def link=element.attributes().get("href")
		if(!link.isEmpty() && !link.startsWith("#") && !link.startsWith(".."))
		{
		
		 	totalList.add(link) 
		 }
     
	}
	
}

def fetchAllContent()
{
	def csv=new File("links.csv")
	def lines = csv.readLines()
	for(str in lines)
	{
		println "Fetching "+"http://www.homeoint.org/books/boericmm/"+str
		writeToFile("html/"+str.substring(str.indexOf("/")+1,str.length()),Jsoup.connect("http://www.homeoint.org/books/boericmm/"+str).get().toString())
	}
	
}


def writeLinkToFile(filename)
{
	def csv=new File(filename)
	
		for(link in totalList)
		{
			csv.append(link+'\n') 
		}
}

def writeToFile(filename,content)
{
	def csv=new File(filename)
		csv.write(content)
}


   static void main(String[] args) { 
    def domain="http://www.homeoint.org/"
	def startUrl="books/boericmm/remedies.htm"
	def fetch=new Fetch()
	//fetch.getPageLinks01("http://www.homeoint.org/books/boericmm/","remedies.htm")	
	//fetch.writeLinkToFile("links.csv")
	fetch.fetchAllContent()
   }
}



	

