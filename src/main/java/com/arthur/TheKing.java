package com.arthur;

import java.util.UUID;

import com.arthur.family.Family;
import com.arthur.family.LifeEvent;
import com.arthur.family.Person;
import com.arthur.util.DoubleKey;
import com.arthur.util.Gender;
import com.arthur.util.LifeEventType;
import com.arthur.util.RelationshipType;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TheKing {


    public static void main(String[] args) {

        TheKing king = new TheKing();
        king.processFamily(args[0]);
    }

    public void processFamily(String filePath) {
        Family family = new Family();
        Set<Person> familyMembers = new HashSet<>();
        familyMembers.clear();
        UUID relativeId = UUID.randomUUID();
        family.setName("Arthur");
        family.setMembers(familyMembers);

        Person theKing = new Person();
        theKing.setFirstName("Arthur");
        theKing.setLastName("Pendragon");
        theKing.setGender(Gender.MALE);
        LocalDate kingDob = LocalDate.now().minusYears(350);
        theKing.setDob(kingDob);

        Person theQueen = new Person();
        theQueen.setFirstName("Margaret");
        theQueen.setLastName("Pendragon");
        theQueen.setGender(Gender.FEMALE);
        LocalDate queenDob = LocalDate.now().minusYears(345);
        theQueen.setDob(queenDob);

        LifeEvent lifeEvent = new LifeEvent();
        lifeEvent.setDate(LocalDate.now());
        lifeEvent.setType(LifeEventType.MARRIAGE);
        setMarriageDivorce(theKing, theQueen, lifeEvent);

        HashMap<DoubleKey, Person> kingRelatives = new HashMap<>();
        DoubleKey key = new DoubleKey(RelationshipType.WIFE, relativeId);
        kingRelatives.put(key, theQueen);
        theKing.setRelatives(kingRelatives);

        relativeId = UUID.randomUUID();
        HashMap<DoubleKey, Person> queenRelatives = new HashMap<>();
        DoubleKey queenkey = new DoubleKey(RelationshipType.HUSBAND, relativeId);
        queenRelatives.put(queenkey, theKing);
        theQueen.setRelatives(queenRelatives);

        familyMembers.add(theKing);
        familyMembers.add(theQueen);

        //=============================

        String fileName = filePath;
        if (fileName.isEmpty()) System.out.println("Please enter the file name to process.");
        File file = new File(fileName);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                System.out.println(st);
                String[] input = st.split(" ");

                if (input[0].toUpperCase().equals("ADD_CHILD")) {
                    String motherName = input[1];
                    String childName = input[2];
                    String gender = input[3];
                    if (!motherName.isEmpty() && !childName.isEmpty() && !gender.isEmpty()) {
                        if (family.getMembers() != null) {

                            Person p = familyMembers.stream().filter(person -> person.getFirstName().equals(motherName)).findFirst().get();

                            if (p != null) {
                                Person child = new Person();
                                child.setFirstName(childName);
                                child.setLastName(p.getLastName());
                                child.setLastName(p.getLastName());
                                if (gender.toUpperCase().equals("MALE")) child.setGender(Gender.MALE);
                                else child.setGender(Gender.FEMALE);
                                child.setDob(LocalDate.now());

                                Person addedChild = addChild(p, child);
                                familyMembers.add(addedChild);
                                System.out.println("CHILD_ADDED");
                            } else {
                                System.out.println("PERSON_NOT_FOUND");
                            }
                        }
                    }
                } else if (input[0].toUpperCase().equals("GET_RELATIONSHIP")) {
                    HashMap<DoubleKey, Person> relatives = null;
                    String name = input[1];
                    String relationship = input[2];
                    if (!name.isEmpty() && !relationship.isEmpty()) {

                        if (family.getMembers() != null) {

                            Person p = familyMembers.stream().filter(person -> person.getFirstName().equals(name)).findFirst().get();
                            RelationshipType theRelation = Arrays.stream(RelationshipType.values())
                                    .filter(e -> e.name().equals(relationship.toUpperCase()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", relationship.toUpperCase())));


                            if (p != null) {
                                relatives = getRelationship(p, theRelation);
                            }
                            if (relatives.isEmpty()) {
                                System.out.print("PERSON_NOT_FOUND");
                            }
                            Iterator relativeIterator = relatives.entrySet().iterator();
                            while (relativeIterator.hasNext()) {
                                Map.Entry relative = (Map.Entry) relativeIterator.next();
                                DoubleKey theKey = (DoubleKey) relative.getKey();
                                Person theRelative = (Person) relative.getValue();
                                System.out.print(theRelative.getFirstName() + " ");
                            }
                            System.out.println("");
                        } else {
                            System.out.println("PERSON_NOT_FOUND");
                        }
                    } else {
                        System.out.println("INCORRECT_FORMAT");
                    }
                } else if (input[0].toUpperCase().equals("MARRY")) {

                    String familyMemberName = input[1];
                    String partnerName = input[2];
                    String partnerGender = input[3];
                    Person familyMember = familyMembers.stream().filter(p -> p.getFirstName().equals(familyMemberName)).findFirst().get();

                    if (familyMember == null) {

                        System.out.println("PERSON_NOT_FOUND");
                        return;
                    }

                    Set<DoubleKey> familyKeySet = familyMember.getRelatives().keySet();
                    for (DoubleKey familyKey : familyKeySet
                    ) {
                        if (familyKey.getRelationship().equals(RelationshipType.HUSBAND) || familyKey.getRelationship().equals((RelationshipType.WIFE))) {
                            System.out.println("NOT_ELIGIBLE");
                            return;
                        }
                    }

                    Person married = marry(familyMember, partnerName, partnerGender);
                    familyMembers.add(married);
                    System.out.println("MARRIED");

                } else if (input[0].toUpperCase().equals("DIE")) {

                    String name = input[1];
                    Person familyMember = familyMembers.stream().filter(p -> p.getFirstName().equals(name)).findFirst().get();
                    if (familyMember == null) {
                        System.out.println("PERSON_NOT_FOUND");
                        return;
                    }
                    die(familyMember);
                    familyMembers.remove(familyMember);
                    System.out.println("RECORDS_REMOVED");

                } else if (input[0].toUpperCase().equals("DIVORCE")) {
                    String familyMemberName = input[1];
                    String divorceeName = input[2];
                    Person familyMember = familyMembers.stream().filter(p -> p.getFirstName().equals(familyMemberName)).findFirst().get();
                    Person divorcee = familyMembers.stream().filter(p -> p.getFirstName().equals(divorceeName)).findFirst().get();
                    divorce(familyMember, divorcee);
                    familyMembers.remove(divorcee);

                    System.out.println("DIVORCED");

                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Please enter the correct file name.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("There was a problem reading the given file.");
            e.printStackTrace();
        }
    }

    public Person addChild(Person mother, Person child) {
        UUID uuid = UUID.randomUUID();
        if (child.getDob().equals(LocalDate.now())) {

            LifeEvent lifeEvent = new LifeEvent();
            lifeEvent.setDate(LocalDate.now());
            lifeEvent.setType(LifeEventType.BIRTH);

            if (child.getLifeEvents() != null) {
                child.getLifeEvents().add(lifeEvent);
            } else {
                Set<LifeEvent> lifeEvents = new HashSet<>();
                lifeEvents.add(lifeEvent);
                child.setLifeEvents(lifeEvents);
            }
        }

        DoubleKey motherKey = new DoubleKey(RelationshipType.MOTHER, 1);
        HashMap<DoubleKey, Person> relatives = new HashMap<>();
        relatives.put(motherKey, mother);
        child.setRelatives(relatives);
        DoubleKey childKey = null;
        if (child.getGender().equals(Gender.MALE)) childKey = new DoubleKey(RelationshipType.SON, uuid);
        else childKey = new DoubleKey(RelationshipType.DAUGHTER, uuid);
        if (mother.getRelatives() != null) {
            mother.getRelatives().put(childKey, child);
        } else {
            HashMap<DoubleKey, Person> momsrelatives = new HashMap<>();
            momsrelatives.put(childKey, child);
            mother.setRelatives(momsrelatives);
        }
        if (mother.getChildren() != null) {
            mother.getChildren().add(child);
        } else {
            Set<Person> children = new HashSet<>();
            children.add(child);
            mother.setChildren(children);
        }
        Person father = null;
        HashMap<DoubleKey, Person> momsRelatives = mother.getRelatives();
        Iterator relIterator = momsRelatives.entrySet().iterator();
        while (relIterator.hasNext()) {
            Map.Entry<DoubleKey, Person> aRelative = (Map.Entry<DoubleKey, Person>) relIterator.next();
            DoubleKey key = aRelative.getKey();
            Person theRelative = aRelative.getValue();

            if (key.getRelationship().equals(RelationshipType.HUSBAND)) {
                theRelative.getRelatives().put(childKey, child);
                DoubleKey fatherKey = new DoubleKey(RelationshipType.FATHER, 1);
                child.getRelatives().put(fatherKey, theRelative);
                father = theRelative;
            } else if (key.getRelationship().equals(RelationshipType.SON) && !theRelative.getFirstName().equals(child.getFirstName())) {

                UUID brotherId = UUID.randomUUID();
                DoubleKey otherBrotherId = new DoubleKey(RelationshipType.BROTHER, brotherId);
                setSiblings(child, theRelative, otherBrotherId);

            } else if (key.getRelationship().equals(RelationshipType.DAUGHTER) && !theRelative.getFirstName().equals(child.getFirstName())) {
                UUID sisterId = UUID.randomUUID();
                DoubleKey otherSisterId = new DoubleKey(RelationshipType.SISTER, sisterId);
                setSiblings(child, theRelative, otherSisterId);
            } else if (key.getRelationship().equals(RelationshipType.BROTHER)) {
                UUID uncleId = UUID.randomUUID();
                DoubleKey doubleKey = new DoubleKey(RelationshipType.MATERNAL_UNCLE, uncleId);
                setNieceNephew(child, theRelative, doubleKey);

            } else if (key.getRelationship().equals(RelationshipType.SISTER)) {
                UUID auntId = UUID.randomUUID();
                DoubleKey doubleKey = new DoubleKey(RelationshipType.MATERNAL_AUNT, auntId);
                setNieceNephew(child, theRelative, doubleKey);

            }
        }

        if (father != null) {

            HashMap<DoubleKey, Person> dadsRelatives = father.getRelatives();
            Iterator dadsRelIterator = dadsRelatives.entrySet().iterator();
            while (dadsRelIterator.hasNext()) {
                Map.Entry<DoubleKey, Person> aRelative = (Map.Entry<DoubleKey, Person>) dadsRelIterator.next();
                DoubleKey key = aRelative.getKey();
                Person theRelative = aRelative.getValue();
                if (key.getRelationship().equals(RelationshipType.BROTHER)
                        && !child.getRelatives().values().contains(theRelative)) {
                    UUID uncleId = UUID.randomUUID();
                    DoubleKey uncleKey = new DoubleKey(RelationshipType.PATERNAL_UNCLE, uncleId);
                    setNieceNephew(child, theRelative, uncleKey);

                } else if (key.getRelationship().equals(RelationshipType.SISTER)
                        && !child.getRelatives().values().contains(theRelative)) {
                    UUID auntId = UUID.randomUUID();
                    DoubleKey auntKey = new DoubleKey(RelationshipType.PATERNAL_AUNT, auntId);
                    setNieceNephew(child, theRelative, auntKey);
                }
            }
        }
        return child;
    }

    public HashMap<DoubleKey, Person> getRelationship(Person originalPerson, RelationshipType type) {
        HashMap<DoubleKey, Person> relatives = new HashMap<>();
        if (originalPerson != null && originalPerson.getRelatives() != null) {
            for (DoubleKey key : originalPerson.getRelatives().keySet()) {
                if (key.getRelationship().equals(type)) {

                    relatives.put(key, originalPerson.getRelatives().get(key));
                }
            }
        }
        return relatives;
    }

    public Person marry(Person familyMember, String partnerName, String partnerGender) {

        HashMap<DoubleKey, Person> partnerRelatives = new HashMap<>();
        Person partner = new Person();
        partner.setFirstName(partnerName);
        partner.setLastName(familyMember.getLastName());

        if (partnerGender.toUpperCase().equals("MALE")) partner.setGender(Gender.MALE);
        else partner.setGender(Gender.FEMALE);

        UUID uuid = UUID.randomUUID();
        DoubleKey familyMemberKey = null;
        if (familyMember.getGender().equals(Gender.MALE)) {
            familyMemberKey = new DoubleKey(RelationshipType.HUSBAND, uuid);
        } else {
            familyMemberKey = new DoubleKey(RelationshipType.WIFE, uuid);
        }
        partnerRelatives.put(familyMemberKey, familyMember);
        partner.setRelatives(partnerRelatives);

        DoubleKey partnerKey = null;
        uuid = UUID.randomUUID();
        if (partner.getGender().equals(Gender.MALE)) {
            partnerKey = new DoubleKey(RelationshipType.HUSBAND, uuid);
        } else {
            partnerKey = new DoubleKey(RelationshipType.WIFE, uuid);
        }
        if (familyMember.getRelatives() != null) {
            familyMember.getRelatives().put(partnerKey, partner);
        } else {

            HashMap<DoubleKey, Person> memberRelatives = new HashMap<>();
            memberRelatives.put(partnerKey, partner);
            familyMember.setRelatives(memberRelatives);
        }

        LifeEvent lifeEvent = new LifeEvent();
        lifeEvent.setDate(LocalDate.now());
        lifeEvent.setType(LifeEventType.MARRIAGE);

        setMarriageDivorce(familyMember, partner, lifeEvent);

        HashMap<DoubleKey, Person> spouseRelatives = familyMember.getRelatives();
        Iterator spouseRelIterator = spouseRelatives.entrySet().iterator();
        while (spouseRelIterator.hasNext()) {
            Map.Entry<DoubleKey, Person> aRelative = (Map.Entry<DoubleKey, Person>) spouseRelIterator.next();
            DoubleKey key = aRelative.getKey();
            Person theRelative = aRelative.getValue();

            UUID id = UUID.randomUUID();
            if (key.getRelationship().equals(RelationshipType.BROTHER)) {
                DoubleKey broInLawKey = new DoubleKey(RelationshipType.BROTHER_IN_LAW, id);
                inLaws(partner, theRelative, broInLawKey);
            } else if (key.getRelationship().equals(RelationshipType.SISTER)) {

                DoubleKey sisInLawKey = new DoubleKey(RelationshipType.SISTER_IN_LAW, id);
                inLaws(partner, theRelative, sisInLawKey);
            }
        }

        return partner;
    }

    public void die(Person theOne) {

        HashMap<DoubleKey, Person> deceasedRelatives = theOne.getRelatives();
        Collection<Person> relatives = deceasedRelatives.values();

        for (Person theRelative : relatives) {
            theRelative.getRelatives().values().remove(theOne);
        }
    }

    public void divorce(Person familyMember, Person divorcee) {

        if (familyMember.getRelatives() != null && familyMember.getRelatives().values().contains(divorcee))
            familyMember.getRelatives().values().remove(divorcee);
        if (divorcee.getRelatives() != null && divorcee.getRelatives().values().contains(familyMember))
            divorcee.getRelatives().values().remove(familyMember);

        LifeEvent lifeEvent = new LifeEvent();
        lifeEvent.setDate(LocalDate.now());
        lifeEvent.setType(LifeEventType.DIVORCE);

        setMarriageDivorce(familyMember, divorcee, lifeEvent);
    }

    private void inLaws(Person partner, Person theRelative, DoubleKey sisInLawKey) {
        if (partner.getRelatives() != null) {
            partner.getRelatives().put(sisInLawKey, theRelative);
        } else {
            HashMap<DoubleKey, Person> newRelatives = new HashMap<>();
            newRelatives.put(sisInLawKey, theRelative);
            partner.setRelatives(newRelatives);
        }
        UUID id = UUID.randomUUID();
        DoubleKey newMemberKey = null;
        if (partner.getGender().equals(Gender.MALE))
            newMemberKey = new DoubleKey(RelationshipType.BROTHER_IN_LAW, id);
        else
            newMemberKey = new DoubleKey(RelationshipType.SISTER_IN_LAW, id);
        theRelative.getRelatives().put(newMemberKey, partner);
    }

    private void setMarriageDivorce(Person familyMember, Person partner, LifeEvent lifeEvent) {
        if (familyMember.getLifeEvents() != null) {
            familyMember.getLifeEvents().add(lifeEvent);
        } else {
            Set<LifeEvent> lifeEvents = new HashSet<>();
            lifeEvents.add(lifeEvent);
            familyMember.setLifeEvents(lifeEvents);
        }

        if (partner.getLifeEvents() != null) {
            partner.getLifeEvents().add(lifeEvent);
        } else {
            Set<LifeEvent> lifeEvents = new HashSet<>();
            lifeEvents.add(lifeEvent);
            partner.setLifeEvents(lifeEvents);
        }
    }

    private void setNieceNephew(Person child, Person theRelative, DoubleKey uncleKey) {
        child.getRelatives().put(uncleKey, theRelative);

        UUID id = UUID.randomUUID();
        DoubleKey doubleKey = null;
        if (child.getGender().equals(Gender.MALE)) doubleKey = new DoubleKey(RelationshipType.NEPHEW, id);
        else doubleKey = new DoubleKey(RelationshipType.NIECE, id);
        theRelative.getRelatives().put(doubleKey, child);
    }

    private void setSiblings(Person child, Person theRelative, DoubleKey otherBrotherId) {
        child.getRelatives().put(otherBrotherId, theRelative);

        UUID id = UUID.randomUUID();
        DoubleKey childSibKey = null;
        if (child.getGender().equals(Gender.MALE)) childSibKey = new DoubleKey(RelationshipType.BROTHER, id);
        else childSibKey = new DoubleKey(RelationshipType.SISTER, id);
        theRelative.getRelatives().put(childSibKey, child);
    }
}
