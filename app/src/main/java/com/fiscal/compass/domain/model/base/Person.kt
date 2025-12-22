package com.fiscal.compass.domain.model.base

import com.fiscal.compass.domain.util.PersonType
import kotlin.random.Random

data class Person(
    val personId: Long = 0,
    val name: String,
    val personType: String,
    val contact: String? = null,

)

object PersonProvider {
    // Individual person providers
    fun providePerson(id: Long = Random.nextLong(1, 1000)): Person {
        val names = listOf("John Doe", "Jane Smith", "Mike Johnson", "Sarah Wilson", "David Brown", "Emily Davis")
        val types = listOf("Individual", "Business", "Freelancer", "Client", "Supplier", "Employee")
        val contacts = listOf("john@email.com", "555-0123", "jane.smith@company.com", "555-0456", null, "mike@freelance.com")

        val index = Random.nextInt(names.size)
        return Person(
            personId = id,
            name = names[index],
            personType = types[index],
            contact = contacts[index]
        )
    }

    fun provideIndividualPerson(id: Long = Random.nextLong(1, 1000)): Person {
        val individualNames = listOf("Alex Thompson", "Maria Garcia", "Chris Lee", "Anna Rodriguez", "Tom Wilson")
        val emails = listOf("alex@email.com", "maria.g@gmail.com", "chris.lee@outlook.com", "anna.r@yahoo.com", "tom.w@email.com")

        val index = Random.nextInt(individualNames.size)
        return Person(
            personId = id,
            name = individualNames[index],
            personType = "Individual",
            contact = emails[index]
        )
    }

    fun provideBusinessPerson(id: Long = Random.nextLong(1, 1000)): Person {
        val businessNames = listOf("Tech Corp LLC", "Green Solutions Inc", "Digital Marketing Co", "Construction Plus", "Health Services Group")
        val businessContacts = listOf("contact@techcorp.com", "555-TECH", "info@greensolutions.com", "555-DIGITAL", "admin@constructionplus.com")

        val index = Random.nextInt(businessNames.size)
        return Person(
            personId = id,
            name = businessNames[index],
            personType = "Business",
            contact = businessContacts[index]
        )
    }

    fun provideFreelancerPerson(id: Long = Random.nextLong(1, 1000)): Person {
        val freelancerNames = listOf("Jessica Designer", "Mark Developer", "Lisa Writer", "Ryan Photographer", "Kate Consultant")
        val freelancerContacts = listOf("jessica@design.com", "mark.dev@freelance.com", "lisa@writing.com", "ryan@photos.com", "kate@consulting.com")

        val index = Random.nextInt(freelancerNames.size)
        return Person(
            personId = id,
            name = freelancerNames[index],
            personType = "Freelancer",
            contact = freelancerContacts[index]
        )
    }

    fun provideClientPerson(id: Long = Random.nextLong(1, 1000)): Person {
        val clientNames = listOf("Global Industries", "Local Restaurant", "Smith Family", "Tech Startup", "Retail Chain")
        val clientContacts = listOf("procurement@global.com", "555-FOOD", "smith.family@email.com", "hello@startup.com", "orders@retail.com")

        val index = Random.nextInt(clientNames.size)
        return Person(
            personId = id,
            name = clientNames[index],
            personType = "Client",
            contact = clientContacts[index]
        )
    }

    fun provideSupplierPerson(id: Long = Random.nextLong(1, 1000)): Person {
        val supplierNames = listOf("Office Supplies Co", "Raw Materials Ltd", "Equipment Rental", "Software Solutions", "Logistics Partner")
        val supplierContacts = listOf("sales@officesupplies.com", "555-MATERIALS", "rent@equipment.com", "support@software.com", "dispatch@logistics.com")

        val index = Random.nextInt(supplierNames.size)
        return Person(
            personId = id,
            name = supplierNames[index],
            personType = "Supplier",
            contact = supplierContacts[index]
        )
    }

    fun provideEmployeePerson(id: Long = Random.nextLong(1, 1000)): Person {
        val employeeNames = listOf("Michael Johnson", "Lisa Anderson", "Robert Taylor", "Jennifer White", "William Brown")
        val employeeContacts = listOf("mjohnson@company.com", "landerson@company.com", "rtaylor@company.com", "jwhite@company.com", "wbrown@company.com")

        val index = Random.nextInt(employeeNames.size)
        return Person(
            personId = id,
            name = employeeNames[index],
            personType = "Employee",
            contact = employeeContacts[index]
        )
    }

    fun provideRandomPerson(id: Long = Random.nextLong(1, 1000)): Person {
        val providers = listOf(
            ::provideIndividualPerson,
            ::provideBusinessPerson,
            ::provideFreelancerPerson,
            ::provideClientPerson,
            ::provideSupplierPerson,
            ::provideEmployeePerson
        )
        return providers.random().invoke(id)
    }

    // List providers
    fun providePersonList(count: Int = 5): List<Person> {
        return (1..count).map { providePerson(it.toLong()) }
    }

    fun provideIndividualPersonList(count: Int = 3): List<Person> {
        return (1..count).map { provideIndividualPerson(it.toLong()) }
    }

    fun provideBusinessPersonList(count: Int = 3): List<Person> {
        return (1..count).map { provideBusinessPerson(it.toLong()) }
    }

    fun provideFreelancerPersonList(count: Int = 2): List<Person> {
        return (1..count).map { provideFreelancerPerson(it.toLong()) }
    }

    fun provideClientPersonList(count: Int = 4): List<Person> {
        return (1..count).map { provideClientPerson(it.toLong()) }
    }

    fun provideSupplierPersonList(count: Int = 3): List<Person> {
        return (1..count).map { provideSupplierPerson(it.toLong()) }
    }

    fun provideEmployeePersonList(count: Int = 5): List<Person> {
        return (1..count).map { provideEmployeePerson(it.toLong()) }
    }

    fun provideMixedPersonList(count: Int = 8): List<Person> {
        return (1..count).map { provideRandomPerson(it.toLong()) }
    }

    fun provideCompletePersonList(): List<Person> {
        return provideIndividualPersonList(2) +
                provideBusinessPersonList(2) +
                provideFreelancerPersonList(2) +
                provideClientPersonList(2) +
                provideSupplierPersonList(1) +
                provideEmployeePersonList(1)
    }

    fun providePersonsByType(personType: String, count: Int = 3): List<Person> {
        return when (personType.lowercase()) {
            "individual" -> provideIndividualPersonList(count)
            "business" -> provideBusinessPersonList(count)
            "freelancer" -> provideFreelancerPersonList(count)
            "client" -> provideClientPersonList(count)
            "supplier" -> provideSupplierPersonList(count)
            "employee" -> provideEmployeePersonList(count)
            else -> provideMixedPersonList(count)
        }
    }

    fun provideLargePersonList(count: Int = 50): List<Person> {
        return (1..count).map { provideRandomPerson(it.toLong()) }
    }
}