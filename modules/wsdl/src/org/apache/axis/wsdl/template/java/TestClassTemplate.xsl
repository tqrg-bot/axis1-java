<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>
    <xsl:template match="/class">
    <xsl:variable name="interfaceName"><xsl:value-of select="@interfaceName"/></xsl:variable>
    <xsl:variable name="package"><xsl:value-of select="@package"/></xsl:variable>
    <xsl:variable name="implpackage"><xsl:value-of select="@implpackage"/></xsl:variable>
    <xsl:variable name="callbackname"><xsl:value-of select="@callbackname"/></xsl:variable>
    <xsl:variable name="stubname"><xsl:value-of select="@stubname"/></xsl:variable>
    <xsl:variable name="isSync"><xsl:value-of select="@isSync"/></xsl:variable>
    <xsl:variable name="isAsync"><xsl:value-of select="@isAsync"/></xsl:variable>
    <xsl:variable name="address"><xsl:value-of select="@address"/></xsl:variable>
    <xsl:variable name="servicexmlpath"><xsl:value-of select="@servicexmlpath"/></xsl:variable>
    package <xsl:value-of select="$package"/>;
    
	import java.io.InputStream;
	import java.net.ServerSocket;
	
	import org.apache.axis.context.ConfigurationContext;
	import org.apache.axis.deployment.DeploymentEngine;
	import org.apache.axis.description.ServiceDescription;
	import org.apache.axis.engine.AxisConfiguration;
	import org.apache.axis.wsdl.codegen.Constants;
	import org.apache.axis.transport.http.SimpleHTTPServer;


    /*
     *  Auto generated Junit test case by the Axis code generator
    */

    public class <xsl:value-of select="@name"/> extends junit.framework.TestCase{
    
    
    private static int count = 0;
	private static SimpleHTTPServer server;
	
	public void setUp() throws Exception {
		if (count == 0) {
			DeploymentEngine deploymentEngine = new DeploymentEngine(System
					.getProperty("user.dir"));
			AxisConfiguration axisConfig = deploymentEngine.load();
			ClassLoader classLoader = this.getClass().getClassLoader();
			classLoader.getResource("<xsl:value-of select="$implpackage"/>.<xsl:value-of select="$interfaceName"/>");
			classLoader.getResource("<xsl:value-of select="$implpackage"/>.<xsl:value-of select="$stubname"/>");
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			InputStream in = cl
					.getResourceAsStream("<xsl:value-of select="$servicexmlpath"/>");
			ServiceDescription service = new ServiceDescription();
			deploymentEngine.buildService(service, in, classLoader);
			
			ConfigurationContext configurationContext = new ConfigurationContext(
					axisConfig);
			ServerSocket serverSoc = null;
			serverSoc = new ServerSocket(Constants.TEST_PORT);
			server = new SimpleHTTPServer(
					configurationContext, serverSoc);
			Thread thread = new Thread(server);
			thread.setDaemon(true);

			try {
				thread.start();
				System.out.print("Server started on port "
						+ Constants.TEST_PORT + ".....");
			} finally {

			}
		}
		count++;
	}

	 protected void tearDown() throws Exception {
	 	 if (count == 1) {
            server.stop();
            count = 0;
            System.out.print("Server stopped .....");
        } else {
            count--;
        }
    }


     <xsl:for-each select="method">
         <xsl:variable name="outputtype"><xsl:value-of select="output/param/@type"></xsl:value-of></xsl:variable>
         <xsl:variable name="inputtype"><xsl:value-of select="input/param/@type"></xsl:value-of></xsl:variable>  <!-- this needs to change-->
         <xsl:variable name="inputparam"><xsl:value-of select="input/param/@name"></xsl:value-of></xsl:variable>  <!-- this needs to change-->
         <xsl:if test="$isSync='1'">

        /**
         * Auto generated test method
         */
        public  void test<xsl:value-of select="@name"/>() throws java.lang.Exception{

        <xsl:value-of select="$implpackage"/>.<xsl:value-of select="$stubname"/> stub = new <xsl:value-of select="$implpackage"/>.<xsl:value-of select="$stubname"/>(".","<xsl:value-of select="$address"/>/<xsl:value-of select="@name"/>");
           <xsl:choose>
             <xsl:when test="$inputtype!=''">
               assertNotNull(stub.<xsl:value-of select="@name"/>(
                                (<xsl:value-of select="$inputtype"/>)createTestInput(<xsl:value-of select="$inputtype"/>.class)));//this should come as a type
              </xsl:when>
              <xsl:otherwise>
                assertNotNull(stub.<xsl:value-of select="@name"/>());
             </xsl:otherwise>
            </xsl:choose>



        }
        </xsl:if>
        <xsl:if test="$isAsync='1'">
            <xsl:variable name="tempCallbackName">tempCallback<xsl:value-of select="generate-id()"/></xsl:variable>
         /**
         * Auto generated test method
         */
        public  void testStart<xsl:value-of select="@name"/>() throws java.lang.Exception{
            <xsl:value-of select="$implpackage"/>.<xsl:value-of select="$stubname"/> stub = new <xsl:value-of select="$implpackage"/>.<xsl:value-of select="$stubname"/>();
             <xsl:choose>
             <xsl:when test="$inputtype!=''">
                stub.start<xsl:value-of select="@name"/>(
                   (<xsl:value-of select="$inputtype"/>)createTestInput(<xsl:value-of select="$inputtype"/>.class),
                    new <xsl:value-of select="$tempCallbackName"/>()
                );
              </xsl:when>
              <xsl:otherwise>
                stub.start<xsl:value-of select="@name"/>(
                    new <xsl:value-of select="$tempCallbackName"/>()
                );
             </xsl:otherwise>
            </xsl:choose>


        }

        private class <xsl:value-of select="$tempCallbackName"/>  extends <xsl:value-of select="$implpackage"/>.<xsl:value-of select="$callbackname"/>{
            public <xsl:value-of select="$tempCallbackName"/>(){ super(null);}

            public void receiveResult<xsl:value-of select="@name"/>(org.apache.axis.clientapi.AsyncResult result) {
			    assertNotNull(result.getResponseEnvelope().getBody().getFirstChild());
            }

            public void receiveError<xsl:value-of select="@name"/>(java.lang.Exception e) {
                fail();
            }

        }
      </xsl:if>
     </xsl:for-each>


     public static Object createTestInput(Class paramClass){

       if (paramClass.equals(String.class)){
           return new String("Test");
       }else if (paramClass.equals(Integer.class)){
            return new Integer(1);
       }else if (paramClass.equals(Float.class)){
           return new Float(2);
       }else if (paramClass.equals(Double.class)){
           return new Double(3);
       //todo this seems to be a long list... needs to complete this
       //}else if (paramClass.equals(OMElement.class)){
       //  return null;
       }else{
         return new Object();
       }

    }
    }
    </xsl:template>
 </xsl:stylesheet>