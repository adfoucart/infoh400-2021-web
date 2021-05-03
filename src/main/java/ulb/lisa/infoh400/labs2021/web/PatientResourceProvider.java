/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.infoh400.labs2021.web;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.instance.model.api.IIdType;
import ulb.lisa.infoh400.labs2020.controller.PatientJpaController;
import ulb.lisa.infoh400.labs2020.services.FHIRServices;

/**
 *
 * @author Adrien Foucart
 */
public class PatientResourceProvider implements IResourceProvider {
    private final EntityManagerFactory emfac = Persistence.createEntityManagerFactory("infoh400_PU");
    private final PatientJpaController patientCtrl = new PatientJpaController(emfac);
    
    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }
    
    // Read method will be accessed by:
    // /fhir/Patient/{id}    
    @Read()
    public Patient getResourceById(@IdParam IIdType pid){        
        /*try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(PatientResourceProvider.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        ulb.lisa.infoh400.labs2020.model.Patient pat = patientCtrl.findPatient(Integer.valueOf(pid.getIdPart()));
        return FHIRServices.getPatient(pat);
    }
    
    
    // Search method will be accessed by:
    // /fhir/Patient?family={name}
    @Search()
    public List<Patient> getPatient(@RequiredParam(name = Patient.SP_FAMILY) StringParam name) { 
        List<ulb.lisa.infoh400.labs2020.model.Patient> patients = patientCtrl.findPatientsByFamilyName(name.getValue());
        List<Patient> fhirPatients = new ArrayList();
        for( ulb.lisa.infoh400.labs2020.model.Patient pat : patients ){
            fhirPatients.add(FHIRServices.getPatient(pat));
        }
        return fhirPatients;
    }
}