package com.sap.solpio.persephone.customer.model;

import org.junit.Assert;
import org.junit.Test;

import com.sap.solpio.persephonecustomer.model.Customer;

public class TestCustomer {

	@Test
	public void testCustomerEntity() {
		
		Customer customer = new Customer();
		customer.setCustomerCategory("1");
		customer.setCustomerCity("Heidelberg");
		customer.setCustomerCountry("DE");
		customer.setCustomerFirstName("ABC");
		customer.setCustomerId("111");
		customer.setCustomerLanguage("EN");
		customer.setCustomerLastName("John");
		customer.setProposalApprover("XYZ");
		customer.setProposalCreator("Bond");
		
		
		Assert.assertTrue(customer.getCustomerLanguage().equals("EN"));
		Assert.assertTrue(customer.getCustomerCategory().equals("1"));
		Assert.assertTrue(customer.getCustomerCity().equals("Heidelberg"));
		Assert.assertTrue(customer.getCustomerCountry().equals("DE"));
		Assert.assertTrue(customer.getCustomerFirstName().equals("ABC"));
		Assert.assertTrue(customer.getCustomerId().equals("111"));
		Assert.assertTrue(customer.getCustomerLastName().equals("John"));
		Assert.assertTrue(customer.getProposalApprover().equals("XYZ"));
		Assert.assertTrue(customer.getProposalCreator().equals("Bond"));
		
		
	}
	
}
