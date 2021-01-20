package com.alodiga.cms.ws;


import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import com.alodiga.authorizer.cms.bean.APIOperations;
import com.alodiga.authorizer.cms.responses.CountryListResponse;
import com.alodiga.authorizer.cms.responses.ProductListResponse;
import com.alodiga.authorizer.cms.responses.ProductResponse;
import com.alodiga.authorizer.cms.responses.TopUpInfoListResponse;
import com.alodiga.authorizer.cms.responses.UserHasProductResponse;

@WebService
public class APIAuthorizerCardManagementSystem {

    private static final Logger logger = Logger.getLogger(APIAuthorizerCardManagementSystem.class);

    @EJB
    private APIOperations operations;

    @WebMethod
    public CountryListResponse getCountryList() {
        return operations.getCountryList();
    }
    
}
