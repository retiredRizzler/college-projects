#include "Observable.h"
#include "Observer.h"

Observable::Observable()
{

}

void Observable::registerObserver(Observer * observer)
{
    observers.insert(observer);
}

void Observable::unregisterObserver(Observer * observer)
{
    observers.erase(observer);
}

void Observable::notifyObservers()
{
    for (Observer * observer : observers)
    {
        observer->update(this);
    }
}

