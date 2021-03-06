/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.javacompat;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import org.neo4j.graphdb.InputPosition;
import org.neo4j.graphdb.Notification;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.SeverityLevel;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.Iterables;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.test.rule.ImpermanentDatabaseRule;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.neo4j.graphdb.impl.notification.NotificationCode.CREATE_UNIQUE_UNAVAILABLE_FALLBACK;
import static org.neo4j.graphdb.impl.notification.NotificationCode.RULE_PLANNER_UNAVAILABLE_FALLBACK;

public class NotificationAcceptanceTest
{
    @Rule
    public final ImpermanentDatabaseRule rule = new ImpermanentDatabaseRule();

    @Test
    public void shouldNotifyWhenUsingCypher3_1ForTheRulePlannerWhenCypherVersionIsTheDefault() throws Exception
    {
        // when
        Result result = db().execute( "CYPHER planner=rule RETURN 1" );
        InputPosition position = new InputPosition( 20, 1, 21 );

        // then
        assertThat( result.getNotifications(), Matchers.<Notification>contains( RULE_PLANNER_UNAVAILABLE_FALLBACK.notification( position ) ) );
        Map<String,Object> arguments = result.getExecutionPlanDescription().getArguments();
        assertThat( arguments.get( "version" ), equalTo( "CYPHER 3.1" ) );
        assertThat( arguments.get( "planner" ), equalTo( "RULE" ) );
        result.close();
    }

    @Test
    public void shouldNotifyWhenUsingCypher3_1ForTheRulePlannerWhenCypherVersionIs3_3() throws Exception
    {
        // when
        Result result = db().execute( "CYPHER 3.3 planner=rule RETURN 1" );
        InputPosition position = new InputPosition( 24, 1, 25 );

        // then
        assertThat( result.getNotifications(), Matchers.<Notification>contains( RULE_PLANNER_UNAVAILABLE_FALLBACK.notification( position ) ) );
        Map<String,Object> arguments = result.getExecutionPlanDescription().getArguments();
        assertThat( arguments.get( "version" ), equalTo( "CYPHER 3.1" ) );
        assertThat( arguments.get( "planner" ), equalTo( "RULE" ) );
        result.close();
    }

    @Test
    public void shouldNotifyWhenUsingCypher3_1ForTheRulePlannerWhenCypherVersionIs3_2() throws Exception
    {
        // when
        Result result = db().execute( "CYPHER 3.2 planner=rule RETURN 1" );
        InputPosition position = new InputPosition( 24, 1, 25 );

        // then
        assertThat( result.getNotifications(), Matchers.<Notification>contains( RULE_PLANNER_UNAVAILABLE_FALLBACK.notification( position ) ) );
        Map<String,Object> arguments = result.getExecutionPlanDescription().getArguments();
        assertThat( arguments.get( "version" ), equalTo( "CYPHER 3.1" ) );
        assertThat( arguments.get( "planner" ), equalTo( "RULE" ) );
        result.close();
    }

    @Test
    public void shouldNotNotifyWhenUsingTheRulePlannerWhenCypherVersionIsNot3_2() throws Exception
    {
        Stream.of( "CYPHER 3.1", "CYPHER 2.3" ).forEach( version ->
        {
            // when
            Result result = db().execute( version + " planner=rule RETURN 1" );

            // then
            assertThat( Iterables.asList( result.getNotifications() ), empty() );
            Map<String,Object> arguments = result.getExecutionPlanDescription().getArguments();
            assertThat( arguments.get( "version" ), equalTo( version ) );
            assertThat( arguments.get( "planner" ), equalTo( "RULE" ) );
            result.close();
        } );
    }

    @Test
    public void shouldNotifyWhenUsingCreateUniqueWhenCypherVersionIsDefault() throws Exception
    {
        // when
        Result result = db().execute( "MATCH (b) WITH b LIMIT 1 CREATE UNIQUE (b)-[:REL]->()" );
        InputPosition position = new InputPosition( 25, 1, 26 );

        // then
        assertThat( result.getNotifications(),
                Matchers.<Notification>contains( CREATE_UNIQUE_UNAVAILABLE_FALLBACK.notification( position ) ) );
        Map<String,Object> arguments = result.getExecutionPlanDescription().getArguments();
        assertThat( arguments.get( "version" ), equalTo( "CYPHER 3.1" ) );
        result.close();
    }

    @Test
    public void shouldNotifyWhenUsingCreateUniqueWhenCypherVersionIs3_3() throws Exception
    {
        // when
        Result result = db().execute( "CYPHER 3.3 MATCH (b) WITH b LIMIT 1 CREATE UNIQUE (b)-[:REL]->()" );
        InputPosition position = new InputPosition( 36, 1, 37 );

        // then
        assertThat( result.getNotifications(),
                Matchers.<Notification>contains( CREATE_UNIQUE_UNAVAILABLE_FALLBACK.notification( position ) ) );
        Map<String,Object> arguments = result.getExecutionPlanDescription().getArguments();
        assertThat( arguments.get( "version" ), equalTo( "CYPHER 3.1" ) );
        result.close();
    }

    @Test
    public void shouldNotifyWhenUsingCreateUniqueWhenCypherVersionIs3_2() throws Exception
    {
        // when
        Result result = db().execute( "CYPHER 3.2 MATCH (b) WITH b LIMIT 1 CREATE UNIQUE (b)-[:REL]->()" );
        InputPosition position = new InputPosition( 36, 1, 37 );

        // then
        assertThat( result.getNotifications(),
                Matchers.<Notification>contains( CREATE_UNIQUE_UNAVAILABLE_FALLBACK.notification( position ) ) );
        Map<String,Object> arguments = result.getExecutionPlanDescription().getArguments();
        assertThat( arguments.get( "version" ), equalTo( "CYPHER 3.1" ) );
        result.close();
    }

    @Test
    public void shouldNotNotifyWhenUsingCreateUniqueWhenCypherVersionIsNot3_2() throws Exception
    {
        Stream.of( "CYPHER 3.1", "CYPHER 2.3" ).forEach( version ->
        {
            // when
            Result result = db().execute( version + " MATCH (b) WITH b LIMIT 1 CREATE UNIQUE (b)-[:REL]->()" );

            // then
            assertThat( Iterables.asList( result.getNotifications() ), empty() );
            Map<String,Object> arguments = result.getExecutionPlanDescription().getArguments();
            assertThat( arguments.get( "version" ), equalTo( version ) );
            result.close();
        } );
    }

    @Test
    public void shouldWarnOnFutureAmbiguousRelTypeSeparator() throws Exception
    {
        for ( String pattern : Arrays.asList( "[:A|:B|:C {foo:'bar'}]", "[:A|:B|:C*]", "[x:A|:B|:C]" ) )
        {
            assertNotifications( "CYPHER 3.3 explain MATCH (a)-" + pattern + "-(b) RETURN a,b",
                    containsItem( notification(
                            "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                            containsString(
                                    "The semantics of using colon in the separation of alternative relationship " +
                                    "types in conjunction with the use of variable binding, inlined property " +
                                    "predicates, or variable length will change in a future version."
                            ),
                            any( InputPosition.class ),
                            SeverityLevel.WARNING ) ) );
        }
    }

    @Test
    public void shouldWarnOnBindingVariableLengthRelationship() throws Exception
    {
        assertNotifications( "CYPHER 3.3 explain MATCH ()-[rs*]-() RETURN rs", containsItem( notification(
                "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                containsString( "Binding relationships to a list in a variable length pattern is deprecated." ),
                any( InputPosition.class ),
                SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingLabel() throws Exception
    {
        assertNotifications( "EXPLAIN MATCH (a:NO_SUCH_THING) RETURN a", containsItem( notification(
                "Neo.ClientNotification.Statement.UnknownLabelWarning",
                containsString( "the missing label name is: NO_SUCH_THING)" ),
                equalTo( new InputPosition( 17, 1, 18 ) ),
                SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingLabelWithCommentInBeginningWithOlderCypherVersions() throws Exception
    {
        assertNotifications( "CYPHER 2.3 EXPLAIN//TESTING \nMATCH (n:X) return n Limit 1", containsItem( notification(
                "Neo.ClientNotification.Statement.UnknownLabelWarning",
                containsString( "the missing label name is: X)" ),
                equalTo( new InputPosition( 38, 2, 10 ) ),
                SeverityLevel.WARNING ) ) );

        assertNotifications( "CYPHER 3.1 EXPLAIN//TESTING \nMATCH (n:X) return n Limit 1", containsItem( notification(
                "Neo.ClientNotification.Statement.UnknownLabelWarning",
                containsString( "the missing label name is: X)" ),
                equalTo( new InputPosition( 38, 2, 10 ) ),
                SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingLabelWithCommentInBeginning() throws Exception
    {
        assertNotifications( "EXPLAIN//TESTING \nMATCH (n:X) return n Limit 1", containsItem( notification(
                "Neo.ClientNotification.Statement.UnknownLabelWarning",
                containsString( "the missing label name is: X)" ),
                equalTo( new InputPosition( 27, 2, 10 ) ),
                SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingLabelWithCommentInBeginningTwoLines() throws Exception
    {
        assertNotifications( "//TESTING \n //TESTING \n EXPLAIN MATCH (n)\n MATCH (b:X) return n,b Limit 1",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.UnknownLabelWarning",
                        containsString( "the missing label name is: X)" ),
                        equalTo( new InputPosition( 52, 4, 11 ) ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingLabelWithCommentInBeginningOnOneLine() throws Exception
    {
        assertNotifications( "explain /* Testing */ MATCH (n:X) RETURN n", containsItem( notification(
                "Neo.ClientNotification.Statement.UnknownLabelWarning",
                containsString( "the missing label name is: X)" ),
                equalTo( new InputPosition( 31, 1, 32 ) ),
                SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingLabelWithCommentInMiddel() throws Exception
    {
        assertNotifications( "EXPLAIN\nMATCH (n)\n//TESTING \nMATCH (n:X)\nreturn n Limit 1",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.UnknownLabelWarning",
                        containsString( "the missing label name is: X)" ),
                        equalTo( new InputPosition( 38, 4, 10 ) ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingRelationshipType() throws Exception
    {
        assertNotifications( "EXPLAIN MATCH ()-[a:NO_SUCH_THING]->() RETURN a", containsItem( notification(
                "Neo.ClientNotification.Statement.UnknownRelationshipTypeWarning",
                containsString( "the missing relationship type is: NO_SUCH_THING)" ),
                any( InputPosition.class ),
                SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingRelationshipTypeWithComment() throws Exception
    {
        assertNotifications( "EXPLAIN /*Comment*/ MATCH ()-[a:NO_SUCH_THING]->() RETURN a", containsItem( notification(
                "Neo.ClientNotification.Statement.UnknownRelationshipTypeWarning",
                containsString( "the missing relationship type is: NO_SUCH_THING)" ),
                equalTo( new InputPosition( 32, 1, 33 ) ),
                SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingProperty() throws Exception
    {
        assertNotifications( "EXPLAIN MATCH (a {NO_SUCH_THING: 1337}) RETURN a", containsItem( notification(
                "Neo.ClientNotification.Statement.UnknownPropertyKeyWarning",
                containsString( "the missing property name is: NO_SUCH_THING)" ),
                any( InputPosition.class ),
                SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForAllNodeScan()
    {
        assertNotifications( "EXPLAIN START n=node(*) RETURN n",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString(
                                "START has been deprecated and will be removed in a future version. (START is " +
                                "deprecated, use: `MATCH (n)`" ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForNodeById()
    {
        assertNotifications( "EXPLAIN START n=node(1337) RETURN n",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString(
                                "START has been deprecated and will be removed in a future version. (START is " +
                                "deprecated, use: `MATCH (n) WHERE id(n) = 1337`" ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForNodeByIds()
    {
        assertNotifications( "EXPLAIN START n=node(42,1337) RETURN n",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString(
                                "START has been deprecated and will be removed in a future version. (START is " +
                                "deprecated, use: `MATCH (n) WHERE id(n) IN [42, 1337]`" ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForNodeIndexSeek()
    {
        try ( Transaction ignore = db().beginTx() )
        {
            db().index().forNodes( "index" );
        }
        assertNotifications( "EXPLAIN START n=node:index(key = 'value') RETURN n",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString( "START has been deprecated and will be removed in a future version. " +
                                        "(START is deprecated, use: " +
                                        "`CALL db.index.explicit.seekNodes('index', 'key', 'value') YIELD node AS n` " +
                                        "instead." ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForNodeIndexSearch()
    {
        try ( Transaction ignore = db().beginTx() )
        {
            db().index().forNodes( "index" );
        }
        assertNotifications( "EXPLAIN START n=node:index('key:value*') RETURN n",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString( "START has been deprecated and will be removed in a future version. " +
                                        "(START is deprecated, use: " +
                                        "`CALL db.index.explicit.searchNodes('index', 'key:value*') YIELD node AS n` " +
                                        "instead." ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForAllRelScan()
    {
        assertNotifications( "EXPLAIN START r=relationship(*) RETURN r",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString(
                                "START has been deprecated and will be removed in a future version. (START is " +
                                "deprecated, use: `MATCH ()-[r]->()`" ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForRelById()
    {
        assertNotifications( "EXPLAIN START r=relationship(1337) RETURN r",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString(
                                "START has been deprecated and will be removed in a future version. (START is " +
                                "deprecated, use: `MATCH ()-[r]->() WHERE id(r) = 1337`" ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForRelByIds()
    {
        assertNotifications( "EXPLAIN START r=relationship(42,1337) RETURN r",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString(
                                "START has been deprecated and will be removed in a future version. (START is " +
                                "deprecated, use: `MATCH ()-[r]->() WHERE id(r) IN [42, 1337]`" ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForRelIndexSeek()
    {
        try ( Transaction ignore = db().beginTx() )
        {
            db().index().forRelationships( "index" );
        }
        assertNotifications( "EXPLAIN START r=relationship:index(key = 'value') RETURN r",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString( "START has been deprecated and will be removed in a future version. " +
                                        "(START is deprecated, use: " +
                                        "`CALL db.index.explicit.seekRelationships('index', 'key', 'value') YIELD " +
                                        "relationship AS r` instead." ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnThatStartIsDeprecatedForRelIndexSearch()
    {
        try ( Transaction ignore = db().beginTx() )
        {
            db().index().forRelationships( "index" );
        }
        assertNotifications( "EXPLAIN START r=relationship:index('key:value*') RETURN r",
                containsItem( notification(
                        "Neo.ClientNotification.Statement.FeatureDeprecationWarning",
                        containsString( "START has been deprecated and will be removed in a future version. " +
                                        "(START is deprecated, use: " +
                                        "`CALL db.index.explicit.searchRelationships('index', 'key:value*') YIELD " +
                                        "relationship AS r` instead." ),
                        any( InputPosition.class ),
                        SeverityLevel.WARNING ) ) );
    }

    @Test
    public void shouldWarnOnMissingPropertyWithComment() throws Exception
    {
        assertNotifications( "EXPLAIN /*Comment*/ MATCH (a {NO_SUCH_THING: 1337}) RETURN a",
                containsItem(
                        notification( "Neo.ClientNotification.Statement.UnknownPropertyKeyWarning",
                                containsString( "the missing property name is: NO_SUCH_THING)" ),
                                equalTo( new InputPosition( 30, 1, 31 ) ),
                                SeverityLevel.WARNING ) ) );
    }

    private void assertNotifications( String query, Matcher<Iterable<Notification>> matchesExpectation )
    {
        try ( Result result = db().execute( query ) )
        {
            assertThat( result.getNotifications(), matchesExpectation );
        }
    }

    private Matcher<Notification> notification(
            String code,
            Matcher<String> description,
            Matcher<InputPosition> position,
            SeverityLevel severity )
    {
        return new TypeSafeMatcher<Notification>()
        {
            @Override
            protected boolean matchesSafely( Notification item )
            {
                return code.equals( item.getCode() ) &&
                       description.matches( item.getDescription() ) &&
                       position.matches( item.getPosition() ) &&
                       severity.equals( item.getSeverity() );
            }

            @Override
            public void describeTo( Description target )
            {
                target.appendText( "Notification{code=" ).appendValue( code )
                        .appendText( ", description=[" ).appendDescriptionOf( description )
                        .appendText( "], position=[" ).appendDescriptionOf( position )
                        .appendText( "], severity=" ).appendValue( severity )
                        .appendText( "}" );
            }
        };
    }

    private GraphDatabaseAPI db()
    {
        return rule.getGraphDatabaseAPI();
    }

    private <T> Matcher<Iterable<T>> containsItem( Matcher<T> itemMatcher )
    {
        return new TypeSafeMatcher<Iterable<T>>()
        {
            @Override
            protected boolean matchesSafely( Iterable<T> items )
            {
                for ( T item : items )
                {
                    if ( itemMatcher.matches( item ) )
                    {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo( Description description )
            {
                description.appendText( "an iterable containing " ).appendDescriptionOf( itemMatcher );
            }
        };
    }
}
