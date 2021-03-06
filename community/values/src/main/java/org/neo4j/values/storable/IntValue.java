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
package org.neo4j.values.storable;

import static java.lang.String.format;

public final class IntValue extends IntegralValue
{
    private final int value;

    IntValue( int value )
    {
        this.value = value;
    }

    public int value()
    {
        return value;
    }

    @Override
    public long longValue()
    {
        return value;
    }

    @Override
    public <E extends Exception> void writeTo( ValueWriter<E> writer ) throws E
    {
        writer.writeInteger( value );
    }

    @Override
    public Object asObjectCopy()
    {
        return value;
    }

    @Override
    public String prettyPrint()
    {
        return Integer.toString( value );
    }

    @Override
    public String toString()
    {
        return format( "Int(%d)", value );
    }
}
