package com.utd.drawasgnmnt;

public interface adapterInterface
{
    void setCurrentSelectedItem(int selection);

    // if both MainActivity and FourthActivity implement this interface, then they both can use the same RecyclerAdapter class code
}
