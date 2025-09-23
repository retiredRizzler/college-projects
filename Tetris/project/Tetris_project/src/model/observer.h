#ifndef OBSERVER_H
#define OBSERVER_H

#include "Observable.h"



class Observer
{
public:
    Observer();
    virtual void update(Observable * subject) = 0;
};




#endif // OBSERVER_H
