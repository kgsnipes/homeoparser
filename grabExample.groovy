@Grapes([
	@Grab('org.jsoup:jsoup:1.10.2'),
    @Grab(group='org.apache.pdfbox', module='pdfbox', version='2.0.0')
	])
import org.jsoup.Jsoup
import java.io.File
import java.io.*
import org.apache.pdfbox.pdmodel.*
import org.apache.pdfbox.text.PDFTextStripper

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

def readPDF()
{
	def pd=null
 	def wr=null
	try {
		
 		def  input = new File("C:\\Users\\kaushik\\Documents\\github\\homeoparser\\medica.pdf")
         def output = new File("C:\\Users\\kaushik\\Documents\\github\\homeoparser\\ActualText.txt")
         pd = PDDocument.load(input)
         println(pd.getNumberOfPages())
         println(pd.isEncrypted())
         //pd.save("CopyOfInvoice.pdf")
         def stripper = new PDFTextStripper()
         stripper.setStartPage(1)//Start extracting from page 3
         stripper.setEndPage(pd.getNumberOfPages()) //Extract till page 5
         wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)))
         stripper.writeText(pd, wr)
         
	}
	catch(Exception e) {
		println(e.getMessage())

		  for(ste in e.getStackTrace())
		  {
		      println(ste.toString())
		  }
	}
	finally {
		if (pd) {
             pd.close()
         }
        if(wr)
        {
        	wr.close()
        }
        
	}
	
}

def analyzeText()
{
	 def medicines=[] as List
	 def textFile = new File("C:\\Users\\kaushik\\Documents\\github\\homeoparser\\ActualText.txt")
	 def lines = textFile.readLines()
	 def count=0
	 for(line in lines)
	 {
	 	if(line.trim().equals("Dose"))
	 	{
	 		markDoseBoundary(count,lines)
	 	}
	 	count++
	 }

	 def fileContent=new StringBuffer()
	 lines.each{String line->fileContent.append(line+"\n")}
	 def markedFile = new File("C:\\Users\\kaushik\\Documents\\github\\homeoparser\\MarkedText.txt")
	 markedFile.write(fileContent.toString())



}

def spiltAndSaveMedicines()
{
	def textFile = new File("C:\\Users\\kaushik\\Documents\\github\\homeoparser\\MarkedText.txt")
	def lines = textFile.readLines()
	def chunk=[] as List
	for(line in lines)
	{
		if(line.trim().equals("###___"))
		{
			def fileContent=new StringBuffer()
			chunk.each{String liner->fileContent.append(liner+"\n")}
			if(chunk[0]!=null)
			{
				def markedFile = new File("C:\\Users\\kaushik\\Documents\\github\\homeoparser\\pdf_content\\"+chunk[0].trim().replaceAll(/\s/,"_")+".txt")
				markedFile.write(fileContent.toString())
				chunk=[] as List
			}
			else
			{
				println "chunk is null "+fileContent.toString()
			}
			
		}
		else
		{
			if(!line.trim().isEmpty())
			{
				chunk.add(line)	
			}
			
		}
	}
	def fileContent=new StringBuffer()
	chunk.each{String line->fileContent.append(line+"\n")}
	def markedFile = new File("C:\\Users\\kaushik\\Documents\\github\\homeoparser\\pdf_content\\"+chunk[0].trim().replaceAll(/\s/,"_")+".txt")
	markedFile.write(fileContent.toString())

}

def markDoseBoundary(count,lines)
{
	def found=false
	while(!found)
	{
		if(lines[count].trim().matches(/\(.*\)/) && !lines[count-1].trim().startsWith("-"))
		{
			lines[count-1]="###___\n"+lines[count-1]
			println lines[count]
			found=!found
		}
		count++
	}
}



   static void main(String[] args) { 
    def domain="http://www.homeoint.org/"
	def startUrl="books/boericmm/remedies.htm"
	def fetch=new Fetch()
	//fetch.getPageLinks01("http://www.homeoint.org/books/boericmm/","remedies.htm")	
	//fetch.writeLinkToFile("links.csv")
	//fetch.fetchAllContent()
	//fetch.readPDF()
	//fetch.analyzeText()
	fetch.spiltAndSaveMedicines()

	println "done"
   }
}


//(\s(\(..*\))*.*(Dose)?.*[\-\*\s].*\.)