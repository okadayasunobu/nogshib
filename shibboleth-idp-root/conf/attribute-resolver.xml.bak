<?xml version="1.0" encoding="UTF-8"?>
<!--
    GakuNin/ConfigTemplate/attribute-resolver-template.xml
    version 4.2.0

    This file is a template provided by GakuNin.
    Based on conf/examples/attribute-resolver-ldap.xml
    in Shibboleth IdP 4.2.1 distribution.
-->
<!--
    This file is an EXAMPLE configuration file containing some example attributes
    based on some commonly used approaches when LDAP is the principal data source.

    Not all attribute definitions or data connectors are demonstrated, but some
    LDAP attributes common to Shibboleth deployments (and some not so common) are
    included.

    This example is in no way usable as a substitute for reading the documentation.
-->
<AttributeResolver
        xmlns="urn:mace:shibboleth:2.0:resolver"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="urn:mace:shibboleth:2.0:resolver http://shibboleth.net/schema/idp/shibboleth-attribute-resolver.xsd">

    <!-- ========================================== -->
    <!--      Attribute Definitions                 -->
    <!-- ========================================== -->

    <!-- Schema: Core schema attributes -->

    <!-- Definition for o -->
    <!--
    <DataConnector id="staticO" xsi:type="Static"
                   exportAttributes="o">
        <Attribute id="o">
            <Value>Test Organization</Value>
        </Attribute>
    </DataConnector>
    -->

    <!-- Schema: eduPerson attributes -->

    <!-- Definition for eduPersonAffiliation -->
    <!--
         This AttributeDefinition must be defined because it is referred by
         eduPersonScopedAffiliation.
    -->
    <!--
    <AttributeDefinition xsi:type="Simple" id="eduPersonAffiliation">
        <InputDataConnector ref="myLDAP" attributeNames="eduPersonAffiliation" />
    </AttributeDefinition>
    -->

    <!-- Definition for eduPersonScopedAffiliation -->
    <!--
    <AttributeDefinition xsi:type="Scoped" id="eduPersonScopedAffiliation" scope="%{idp.scope}">
        <InputAttributeDefinition ref="eduPersonAffiliation" />
    </AttributeDefinition>
    -->

    <!-- Definition for eduPersonEntitlement -->
    <!--
    <AttributeDefinition xsi:type="Simple" id="eduPersonEntitlement">
        <InputDataConnector ref="staticEntitlementCommonLibTerms" attributeNames="eduPersonEntitlement" />
    </AttributeDefinition>
    -->
    <!-- Static Connector for eduPersonEntitlement (common-lib-terms) -->
    <!--
    <DataConnector id="staticEntitlementCommonLibTerms" xsi:type="Static">
        <Attribute id="eduPersonEntitlement">
            <Value>urn:mace:dir:entitlement:common-lib-terms</Value>
        </Attribute>
    </DataConnector>
    -->

    <!-- Definition for eduPersonPrincipalName -->
    <!---->
    <AttributeDefinition xsi:type="Scoped" id="eduPersonPrincipalName" scope="%{idp.scope}">
        <InputDataConnector ref="nogshib-ldap" attributeNames="uid" />
    </AttributeDefinition>
    <!---->

    <!-- Definition for eduPersonUniqueId -->
    <!--
    <AttributeDefinition xsi:type="Scoped" id="eduPersonUniqueId" scope="%{idp.scope}">
        <InputDataConnector ref="myLDAP" attributeNames="eduPersonUniqueId" />
    </AttributeDefinition>
    -->

    <!-- Definition for eduPersonTargetedID (computedID) -->
    <!--
    <AttributeDefinition xsi:type="SAML2NameID" id="eduPersonTargetedID" nameIdFormat="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent">
        <InputDataConnector ref="computedID" attributeNames="computedID" />
    </AttributeDefinition>
    -->
    <!-- Computed targeted ID connector -->
    <!--
    <DataConnector id="computedID" xsi:type="ComputedId"
                   excludeResolutionPhases="c14n/attribute"
                   generatedAttributeID="computedID"
                   salt="%{idp.persistentId.salt}"
                   algorithm="%{idp.persistentId.algorithm:SHA}"
                   encoding="%{idp.persistentId.encoding:BASE64}">
        <InputDataConnector ref="myLDAP" attributeNames="%{idp.persistentId.sourceAttribute}" />
    </DataConnector>
    -->

    <!-- Definition for eduPersonTargetedID (storedID) -->
    <!--
    <AttributeDefinition xsi:type="SAML2NameID" id="eduPersonTargetedID" nameIdFormat="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent">
        <InputDataConnector ref="storedID" attributeNames="storedID" />
    </AttributeDefinition>
    -->
    <!-- Stored targeted ID connector -->
    <!--
        Make sure of the following MyDataSource configuration in conf/global.xml in order to use
        the below StoredIdConnector:

          <bean id="MyDataSource"
                class="org.apache.commons.dbcp2.BasicDataSource"
                p:driverClassName="com.mysql.jdbc.Driver"
                p:url="jdbc:mysql://localhost:3306/*****DB_NAME*****"
                p:username="*****DB_USERNAME*****"
                p:password="*****USER_PASSWORD*****"
                p:maxTotal="10"
                p:maxIdle="5"
                p:maxWaitMillis="15000"
                p:testOnBorrow="true"
                p:validationQuery="select 1"
                p:validationQueryTimeout="5" />

        For more detail, see: https://meatwiki.nii.ac.jp/confluence/display/GakuNinShibInstall/StoredID
    -->
    <!--
    <DataConnector id="storedID" xsi:type="StoredId"
                   excludeResolutionPhases="c14n/attribute"
                   generatedAttributeID="storedID"
                   salt="%{idp.persistentId.salt}"
                   algorithm="%{idp.persistentId.algorithm:SHA}"
                   encoding="%{idp.persistentId.encoding:BASE64}">
        <InputDataConnector ref="myLDAP" attributeNames="%{idp.persistentId.sourceAttribute}" />
        <BeanManagedConnection>MyDataSource</BeanManagedConnection>
    </DataConnector>
    -->

    <!-- Schema: eduMember attributes -->

    <!-- Definition of isMemberOf -->
    <!--
         Here are 2 examples depending on sources. Choose one and customize
         appropriately.
    -->
    <!-- isMemberOf from LDAP -->
    <!--
    <AttributeDefinition xsi:type="Simple" id="isMemberOf">
        <InputDataConnector ref="myLDAP" attributeNames="isMemberOf" />
    </AttributeDefinition>
    -->
    <!-- isMemberOf from inline sources -->
    <!--
    <AttributeDefinition xsi:type="Mapped" id="isMemberOf">
        <InputDataConnector ref="myLDAP" attributeNames="uid" />
        <ValueMap>
            <ReturnValue>https://voplatform.example.ac.jp/gr/FooGroup</ReturnValue>
            <SourceValue>test001</SourceValue>
            <SourceValue>test002</SourceValue>
            <SourceValue>test003</SourceValue>
        </ValueMap>
    </AttributeDefinition>
    -->

    <!-- Schema: GakuNin attributes -->

    <!-- Definition of jao -->
    <!--
    <DataConnector id="staticJaO" xsi:type="Static"
                   exportAttributes="jao">
        <Attribute id="jao">
            <Value>テスト大学</Value>
        </Attribute>
    </DataConnector>
    -->

    <!-- Definition of gakuninScopedPersonalUniqueCode (LDAP) -->
    <!--
    <AttributeDefinition xsi:type="Prescoped" id="gakuninScopedPersonalUniqueCode">
        <InputDataConnector ref="myLDAP" attributeNames="gakuninScopedPersonalUniqueCode" />
    </AttributeDefinition>
    -->

    <!-- Definition of gakuninScopedPersonalUniqueCode (Template Attribute) -->
    <!--
    <AttributeDefinition xsi:type="Scoped" id="gakuninScopedPersonalUniqueCode" scope="%{idp.scope}">
        <InputAttributeDefinition ref="templatePersonalUniqueCode" />
    </AttributeDefinition>

    <AttributeDefinition xsi:type="Template" id="templatePersonalUniqueCode">
        <InputDataConnector ref="myLDAP" attributeNames="employeeNumber" />
        <Template>
            <![CDATA[
                facultyorstaff:${employeeNumber}
            ]]>
        </Template>
    </AttributeDefinition>
    -->

    <!-- ========================================== -->
    <!--      Data Connectors                       -->
    <!-- ========================================== -->

    <!-- Example LDAP Connector -->
    <!--
        Add the attributes that you want to send directly to the exportAttributes
        attribute. the exportAttributes is a space-separated list, which can contain:
          mail sn ou givenName displayName eduPersonAssurance eduPersonOrcid
          jasn jaGivenName jaDisplayName jaou

        e.g.
          exportAttributes="mail sn ou givenName displayName jasn jaGivenName jaDisplayName jaou"

        If you have an LDAP attribute name which is different from the above list,
        change the columnName attribute in the corresponding Column element
        approprietely and enable that line.

        e.g.
          <Column columnName="email" attributeID="mail" />


        Add the following configuration below the <DataConnector line in order
        to use StartTLS (i.e. idp.attribute.resolver.LDAP.useStartTLS in
        conf/ldap.properties is true) or LDAPS (i.e. you use "ldaps:" protocol
        in idp.attribute.resolver.LDAP.ldapURL in conf/ldap.properties):

          trustFile="%{idp.attribute.resolver.LDAP.trustCertificates}"
    -->
    <!-- -->
    <DataConnector id="nogshib-ldap" xsi:type="LDAPDirectory"
        ldapURL="%{idp.attribute.resolver.LDAP.ldapURL}"
        baseDN="%{idp.attribute.resolver.LDAP.baseDN}"
        principal="%{idp.attribute.resolver.LDAP.bindDN}"
        principalCredential="%{idp.attribute.resolver.LDAP.bindDNCredential}"
        useStartTLS="%{idp.attribute.resolver.LDAP.useStartTLS:true}"
        connectTimeout="%{idp.attribute.resolver.LDAP.connectTimeout}"
        responseTimeout="%{idp.attribute.resolver.LDAP.responseTimeout}"
        connectionStrategy="%{idp.attribute.resolver.LDAP.connectionStrategy:ACTIVE_PASSIVE}"
        noResultIsError="%{idp.attribute.resolver.LDAP.noResultIsError:true}"
        multipleResultsIsError="true"
        excludeResolutionPhases="c14n/attribute"
        exportAttributes="CHANGETHISTOATTRIBUTESYOUWANTTOSEND">
        <FilterTemplate>
            <![CDATA[
                %{idp.attribute.resolver.LDAP.searchFilter}
            ]]>
        </FilterTemplate>
        <ConnectionPool
            minPoolSize="%{idp.pool.LDAP.minSize:3}"
            maxPoolSize="%{idp.pool.LDAP.maxSize:10}"
            blockWaitTime="%{idp.pool.LDAP.blockWaitTime:PT3S}"
            validatePeriodically="%{idp.pool.LDAP.validatePeriodically:true}"
            validateTimerPeriod="%{idp.pool.LDAP.validatePeriod:PT5M}"
            validateDN="%{idp.pool.LDAP.validateDN:}"
            validateOnCheckout="%{idp.pool.LDAP.validateOnCheckout:false}"
            validateFilter="%{idp.pool.LDAP.validateFilter:(objectClass=*)}"
            prunePeriod="%{idp.pool.LDAP.prunePeriod:PT5M}"
            expirationTime="%{idp.pool.LDAP.idleTime:PT10M}"/>
        <!--
        <Column columnName="mail" attributeID="mail" />
        <Column columnName="sn" attributeID="sn" />
        <Column columnName="ou" attributeID="ou" />
        <Column columnName="givenName" attributeID="givenName" />
        <Column columnName="displayName" attributeID="displayName" />
        <Column columnName="eduPersonAssurance" attributeID="eduPersonAssurance" />
        <Column columnName="eduPersonOrcid" attributeID="eduPersonOrcid" />
        <Column columnName="sn;lang-ja" attributeID="jasn" />
        <Column columnName="givenName;lang-ja" attributeID="jaGivenName" />
        <Column columnName="displayName;lang-ja" attributeID="jaDisplayName" />
        <Column columnName="ou;lang-ja" attributeID="jaou" />
        -->
    </DataConnector>
    <!-- -->

</AttributeResolver>
