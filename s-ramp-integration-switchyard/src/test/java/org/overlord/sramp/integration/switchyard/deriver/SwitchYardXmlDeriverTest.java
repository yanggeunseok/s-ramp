/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.sramp.integration.switchyard.deriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactEnum;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.Document;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.ExtendedDocument;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.Relationship;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.overlord.sramp.common.derived.LinkerContext;
import org.overlord.sramp.integration.switchyard.model.SwitchYardModel;

/**
 * JUnit test for {@link SwitchYardXmlDeriver}.
 *
 * @author eric.wittmann@redhat.com
 */
public class SwitchYardXmlDeriverTest {

    /**
     * Test method for {@link org.overlord.sramp.integration.switchyard.deriver.SwitchYardXmlDeriver#derive(java.util.Collection, org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType, org.w3c.dom.Element, javax.xml.xpath.XPath)}.
     * @throws IOException
     */
    @Test
    public void testSwitchyardDeriver() throws IOException {
        SwitchYardXmlDeriver deriver = new SwitchYardXmlDeriver();
        ExtendedDocument artifact = new ExtendedDocument();
        artifact.setArtifactType(BaseArtifactEnum.EXTENDED_ARTIFACT_TYPE);
        artifact.setName("switchyard.xml"); //$NON-NLS-1$
        artifact.setExtendedType(SwitchYardModel.SwitchYardXmlDocument);
        InputStream is = getClass().getResourceAsStream("switchyard.xml"); //$NON-NLS-1$
        // Derive
        Collection<BaseArtifactType> derivedArtifacts = deriver.derive(artifact, is);

        // Asserts
        Assert.assertNotNull(derivedArtifacts);
        Assert.assertEquals(9, derivedArtifacts.size());
        Assert.assertEquals("orders", artifact.getName()); //$NON-NLS-1$
        Assert.assertEquals("urn:switchyard-quickstart:bean-service:0.1.0", SrampModelUtils.getCustomProperty(artifact, "targetNamespace")); //$NON-NLS-1$ //$NON-NLS-2$

        BaseArtifactType orderService = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardServiceType, "OrderService"); //$NON-NLS-1$
        Assert.assertNotNull(orderService);
        Assert.assertEquals("OrderService", orderService.getName()); //$NON-NLS-1$


        BaseArtifactType inventoryServiceComponent = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardComponentType, "InventoryService"); //$NON-NLS-1$
        Assert.assertNotNull(inventoryServiceComponent);
        Assert.assertEquals("managedTransaction.Global", SrampModelUtils.getCustomProperty(inventoryServiceComponent, "requires")); //$NON-NLS-1$ //$NON-NLS-2$
        Relationship relationship = SrampModelUtils.getGenericRelationship(inventoryServiceComponent, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(inventoryServiceComponent, SwitchYardModel.REL_REFERENCES);
        Assert.assertNull(relationship);


        BaseArtifactType orderServiceComponent = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardComponentType, "OrderService"); //$NON-NLS-1$
        Assert.assertNotNull(orderServiceComponent);
        relationship = SrampModelUtils.getGenericRelationship(orderServiceComponent, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(orderServiceComponent, SwitchYardModel.REL_REFERENCES);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(orderService, SwitchYardModel.REL_PROMOTES);
        Assert.assertNotNull(relationship);
        Assert.assertNotNull(relationship.getRelationshipTarget());
        Assert.assertFalse(relationship.getRelationshipTarget().isEmpty());
        Assert.assertEquals(orderServiceComponent.getUuid(), relationship.getRelationshipTarget().iterator().next().getValue());


        BaseArtifactType camelServiceComponent = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardComponentType, "CamelService"); //$NON-NLS-1$
        Assert.assertNotNull(camelServiceComponent);
        Assert.assertEquals("noManagedTransaction", SrampModelUtils.getCustomProperty(camelServiceComponent, "requires")); //$NON-NLS-1$ //$NON-NLS-2$
        relationship = SrampModelUtils.getGenericRelationship(camelServiceComponent, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(camelServiceComponent, SwitchYardModel.REL_REFERENCES);
        Assert.assertNull(relationship);


        BaseArtifactType transformJava = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardTransformerType, "OrderAck->submitOrderResponse"); //$NON-NLS-1$
        Assert.assertNotNull(transformJava);
        Assert.assertEquals("java", SrampModelUtils.getCustomProperty(transformJava, SwitchYardModel.PROP_TRANSFORMER_TYPE)); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_TRANSFORMS_FROM);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_TRANSFORMS_TO);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));


        BaseArtifactType transformXslt = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardTransformerType, "CDM->S1"); //$NON-NLS-1$
        Assert.assertNotNull(transformXslt);
        Assert.assertEquals("xslt", SrampModelUtils.getCustomProperty(transformXslt, SwitchYardModel.PROP_TRANSFORMER_TYPE)); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(transformXslt, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_TRANSFORMS_FROM);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_TRANSFORMS_TO);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));


        BaseArtifactType validateJava = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardValidatorType, "java:org.switchyard.quickstarts.bean.service.Order"); //$NON-NLS-1$
        Assert.assertNotNull(validateJava);
        Assert.assertEquals("java", SrampModelUtils.getCustomProperty(validateJava, SwitchYardModel.PROP_VALIDATE_TYPE)); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(validateJava, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(validateJava, SwitchYardModel.REL_VALIDATES);
        Assert.assertNotNull(relationship);
        Assert.assertTrue(relationship.getRelationshipTarget().isEmpty());
        Assert.assertTrue(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));


        // Now pretend to do the linking (ensure we always find a match for every unresolved reference).
        LinkerContext context = new LinkerContext() {
            @Override
            public Collection<BaseArtifactType> findArtifacts(String model, String type, Map<String, String> criteria) {
                BaseArtifactType doc = new Document();
                doc.setArtifactType(BaseArtifactEnum.DOCUMENT);
                doc.setName("Mock Artifact"); //$NON-NLS-1$
                doc.setUuid(UUID.randomUUID().toString());
                return Collections.singletonList(doc);
            }
        };
        deriver.link(context , artifact, derivedArtifacts);


        inventoryServiceComponent = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardComponentType, "InventoryService"); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(inventoryServiceComponent, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(inventoryServiceComponent, SwitchYardModel.REL_REFERENCES);
        Assert.assertNull(relationship);


        orderServiceComponent = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardComponentType, "OrderService"); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(orderServiceComponent, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(orderServiceComponent, SwitchYardModel.REL_REFERENCES);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));


        camelServiceComponent = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardComponentType, "CamelService"); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(camelServiceComponent, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        // TODO Note - looking up a camel xml is not yet implemented
//        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));


        transformJava = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardTransformerType, "OrderAck->submitOrderResponse"); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_TRANSFORMS_FROM);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_TRANSFORMS_TO);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));


        transformXslt = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardTransformerType, "CDM->S1"); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(transformXslt, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        // TODO: Note - XSLT transforms not yet implemented (see SwitchYardLinker)
//        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_TRANSFORMS_FROM);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(transformJava, SwitchYardModel.REL_TRANSFORMS_TO);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));

        validateJava = getDerivedArtifact(derivedArtifacts, SwitchYardModel.SwitchYardValidatorType, "java:org.switchyard.quickstarts.bean.service.Order"); //$NON-NLS-1$
        relationship = SrampModelUtils.getGenericRelationship(validateJava, SwitchYardModel.REL_IMPLEMENTED_BY);
        Assert.assertNotNull(relationship);
        // TODO: Note - CDI beans not yet implemented (see SwitchYardLinker)
//        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
        relationship = SrampModelUtils.getGenericRelationship(validateJava, SwitchYardModel.REL_VALIDATES);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(1, relationship.getRelationshipTarget().size());
        Assert.assertFalse(relationship.getOtherAttributes().containsKey(SwitchYardXmlDeriver.UNRESOLVED_REF));
    }

    /**
     * Gets a single derived artifact from the collection of derived artifacts.  Narrows
     * it down by type and name.
     * @param derivedArtifacts
     * @param artifactType
     * @param name
     */
    private BaseArtifactType getDerivedArtifact(Collection<BaseArtifactType> derivedArtifacts,
            ArtifactType artifactType, String name) {
        for (BaseArtifactType artifact : derivedArtifacts) {
            ArtifactType at = ArtifactType.valueOf(artifact);
            if (at.equals(artifactType) && artifact.getName().equals(name)) {
                return artifact;
            }
        }
        return null;
    }

}
