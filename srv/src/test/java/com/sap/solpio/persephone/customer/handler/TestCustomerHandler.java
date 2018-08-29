package com.sap.solpio.persephone.customer.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataExceptionType;
import com.sap.cloud.sdk.s4hana.connectivity.ErpEndpoint;
import com.sap.cloud.sdk.s4hana.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;
import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.exception.DataConversionException;
import com.sap.cloud.sdk.service.prov.api.request.CreateRequest;
import com.sap.cloud.sdk.service.prov.api.request.MessageContainerImpl;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.response.impl.CreateResponseImpl;
import com.sap.cloud.sdk.service.prov.api.response.impl.ErrorResponseImpl;
import com.sap.cloud.sdk.service.prov.api.response.impl.QueryResponseImpl;
import com.sap.cloud.sdk.service.prov.api.response.impl.ReadResponseImpl;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.solpio.persephonecustomer.customerhandler.CustomerHandler;
import com.sap.solpio.persephonecustomer.model.Customer;

public class TestCustomerHandler
{
    private static final MockUtil mockUtil = new MockUtil();
    private static BusinessPartner alice;
    private static BusinessPartner bob;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
    	//Use S4Hana MockUtil to mock platform calls for unit tests
        mockUtil.mockDefaults();
        mockUtil.mockDestination("S4HANA_basic", URI.create(""));

        //workaround required to set language locale for odata else it throws exception for no resource
		@SuppressWarnings("unused")
		MessageContainerImpl messageImpl = new MessageContainerImpl(Locale.ENGLISH);

		//create mock BuPa objects
        List<BusinessPartnerAddress> addrList = new ArrayList<>();
        BusinessPartnerAddress businessPartnerAddress = new BusinessPartnerAddress();
        businessPartnerAddress.setCityName("Heidelberg");
        businessPartnerAddress.setCountry("DE");
		addrList.add(businessPartnerAddress);
        alice = new BusinessPartner();
        alice.setFirstName("Alice");
        alice.setCustomer("alice");
        alice.setBusinessPartnerCategory("1");
        alice.setCustomField("YY1_ApprovedBy_bus", "don");
        alice.setCustomField("YY1_ProposedBy_bus", "ron");
		alice.setBusinessPartnerAddress(addrList);
        bob = new BusinessPartner();
        bob.setFirstName("Bob");
        bob.setCustomer("bob");
        bob.setCustomField("YY1_ApprovedBy_bus", null);
        bob.setCustomField("YY1_ProposedBy_bus", null);
        bob.setBusinessPartnerCategory("1");
        bob.setBusinessPartnerAddress(addrList);

		
    }

    @SuppressWarnings("unchecked")
	@Test
    public void  testQueryCustomer() throws Exception
    {
    	//setup mock
        final BusinessPartnerService service = Mockito.mock(BusinessPartnerService.class, RETURNS_DEEP_STUBS);
        when(service.getAllBusinessPartner()
        		.filter(any(ExpressionFluentHelper.class))
        		.filter(any(ExpressionFluentHelper.class))
        		.select(any())
        		.execute(any(ErpEndpoint.class))).thenReturn(Lists.newArrayList(alice, bob));
        CustomerHandler customerHandler = new CustomerHandler();
        customerHandler.setBupaService(service);
        
        //test
		QueryResponseImpl queryResponse = (QueryResponseImpl)customerHandler.queryA_Customer(null);
        List<?> data = queryResponse.getPojoData();
        Assert.assertTrue(data.size() == 2);
    }


    @Test
    public void testReadCustomerWithInvalidRequest() throws Exception {
    	//setup mock
    	final BusinessPartnerService service = Mockito.mock(BusinessPartnerService.class, RETURNS_DEEP_STUBS);
        when(service.getBusinessPartnerByKey("alice")
        		.select(any())
        		.execute(any(ErpEndpoint.class))).thenReturn(alice);
        CustomerHandler customerHandler = new CustomerHandler();
        customerHandler.setBupaService(service);
        ReadRequest readReq = new ReadRequest() {
			
			@Override
			public Map<String, Object> getSourceKeys() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, Object> getKeys() {
				Map<String,Object> content = new HashMap<>();
				return content;
			}
		};

		//test
		ReadResponseImpl readResponse = (ReadResponseImpl)customerHandler.readA_Customer(readReq);
        ErrorResponseImpl errorResponse = (ErrorResponseImpl)readResponse.getErrorResponse();
        Assert.assertTrue(errorResponse.getMessage().equals("Invalid Request.Missing business partner id for request"));
    	
    }

    @Test
    public void testReadCustomerWithEmptyKeyMap() throws Exception {
        final BusinessPartnerService service = Mockito.mock(BusinessPartnerService.class, RETURNS_DEEP_STUBS);
        when(service.getBusinessPartnerByKey("alice")
        		.select(any())
        		.execute(any(ErpEndpoint.class))).thenThrow(new ODataException(ODataExceptionType.METADATA_FETCH_FAILED, "Failed to get specified customer"));
        CustomerHandler customerHandler = new CustomerHandler();
        customerHandler.setBupaService(service);
        
        ReadRequest readReq = new ReadRequest() {
			
			@Override
			public Map<String, Object> getSourceKeys() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, Object> getKeys() {
				Map<String,Object> content = new HashMap<>();
				content.put("CustomerId", "alice");
				return content;
			}
		};
        
		ReadResponseImpl readResponse = (ReadResponseImpl)customerHandler.readA_Customer(readReq);
        ErrorResponseImpl errorResponse = (ErrorResponseImpl)readResponse.getErrorResponse();
        Assert.assertTrue(errorResponse.getMessage().equals("Read error occurred for business partner from S/4Hana. Failed to get specified customer"));
    	
    }
    
 	@Test
    public void  testReadCustomerValid() throws Exception
    {
        final BusinessPartnerService service = Mockito.mock(BusinessPartnerService.class, RETURNS_DEEP_STUBS);
        when(service.getBusinessPartnerByKey("alice")
        		.select(any())
        		.execute(any(ErpEndpoint.class))).thenReturn(alice);
        CustomerHandler customerHandler = new CustomerHandler();
        customerHandler.setBupaService(service);
        
        ReadRequest readReq = new ReadRequest() {
			
			@Override
			public Map<String, Object> getSourceKeys() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, Object> getKeys() {
				Map<String,Object> content = new HashMap<>();
				content.put("CustomerId", "alice");
				return content;
			}
		};
        
		ReadResponseImpl readResponse = (ReadResponseImpl)customerHandler.readA_Customer(readReq);
        Customer data = (Customer)readResponse.getData();
        Assert.assertTrue(data.getCustomerCountry().equals("DE"));
    }
    

 	@Test
 	public void testCreateCustomer() throws ODataException {
 		//create create-request
		CreateRequest createReq = new CreateRequest() {
			
			@Override
			public Map<String, Object> getSourceKeys() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, Object> getMapData() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T> T getDataAs(Class<T> arg0) throws DataConversionException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EntityData getData() {
				return getDummyCustomer();
			}
		};
 		
        final BusinessPartnerService service = Mockito.mock(BusinessPartnerService.class, RETURNS_DEEP_STUBS);
        when(service
        		.createBusinessPartner(any(BusinessPartner.class))
        		.execute(any(ErpEndpoint.class))).thenReturn(alice);
        CustomerHandler customerHandler = new CustomerHandler();
        customerHandler.setBupaService(service);
 		
		CreateResponseImpl createResponseImpl = (CreateResponseImpl)customerHandler.writeA_Customer(createReq);
        Customer data = (Customer)createResponseImpl.getData();
        Assert.assertTrue(data.getCustomerCountry().equals("DE"));
 	}
 
	private EntityData getDummyCustomer() {
		Map<String, Object> propertiesMap = new HashMap<>();
		propertiesMap.put("CustomerFirstName", "Demo");
		propertiesMap.put("CustomerLastName", "John");
		propertiesMap.put("CustomerCity", "Heidelberg");
		propertiesMap.put("CustomerCountry", "DE");
		propertiesMap.put("ProposalApprover", "XYZ");
		propertiesMap.put("ProposalCreator", "ABC");
		
		List<String> keys = new ArrayList<>();
		EntityData customer = EntityData.createFromMap(propertiesMap, keys, "Customer");
		return customer;
	}
	
}