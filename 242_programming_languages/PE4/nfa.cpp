#include "nfa.hpp"

// private:
bool NFA::has_state(const std::string& state) const{
    for(std::string a :all_states)
    {
        if(a == state)
        {
            return true;
        }
    }
    return false;
}

bool NFA::is_final_state(const std::string& state) const{
    for(std::string a :final_states)
    {
        if(a == state)
        {
            return true;
        }
    }
    return false;
}

// public:
bool NFA::is_DFA() const{
    std::set<std::string> next_states;
    for(std::string q : all_states)
    {
        for(char b : alphabet.get_symbols())
        {
            next_states = transitions(q,b);
            if(next_states.size() != 1)
            {
                return false;
            }
        }
    }
    return true;
}

void NFA::update_state_name(const std::string& old_name, const std::string& new_name){
    for(std::string a : all_states)
    {
        if (a == old_name)
        {
            a = new_name;
        }
    }
    for(std::string a : final_states)
    {
        if (a == old_name)
        {
            a = new_name;
        }
    }
    if(starting_state == old_name)
    {
        starting_state = new_name;
    }
    transitions.update_state_name(old_name,new_name);
}

bool NFA::process(std::string input){
    if(!alphabet.is_valid(input))
    {
        std::cout << "Invalid string" << std::endl;
        return false;
    }
    std::queue<ComputationBranch> q;
    q.push(ComputationBranch(starting_state,input));
    std::string curr_state, curr_input, rest_input;
    ComputationBranch branch;
    while(!q.empty())
    {
        branch = q.front();
        curr_state = branch.get_last_config().first;
        curr_input = branch.get_last_config().second;

        q.pop();
        if(curr_input.empty() && is_final_state(curr_state))
        {
            std::cout <<branch<< std::endl;
            std::cout << "Accept" << std::endl;
            return true;
        }

        std::set<std::string> next_states = transitions(curr_state,'e');
        for(std::string ns : next_states) //check empty transitions and push
        {
            q.push(ComputationBranch(ns,curr_input));
        }

        if(!curr_input.empty())
        {
            char next_sym = curr_input[0];
            next_states = transitions(curr_state,next_sym);
            rest_input = curr_input.substr(1);
            for(std::string ns : next_states)
            {
                q.push(ComputationBranch(ns,rest_input));
            }            
        }
    }
    std::cout<<branch<<std::endl;
    std::cout<<"Reject"<<std::endl;
    return false;
}
    
NFA NFA::operator!() const{
    NFA newNFA;
    if(!is_DFA())
    {
        std::cout<<"Not a DFA" << std::endl;
        return (*this);
    }
    std::set<std::string> new_finals;
    for(std::string f: all_states)
    {
        if(final_states.find(f) == final_states.end())
        {
            new_finals.insert(f);
        }
    }
    newNFA.all_states = (*this).all_states;
    newNFA.final_states = new_finals;
    newNFA.alphabet = (*this).alphabet;
    newNFA.starting_state = (*this).starting_state;
    newNFA.transitions = (*this).transitions;
    return newNFA;
}

NFA NFA::operator+(const NFA& other) const{
    NFA newNFA(*this);
    NFA tempNFA = other;
    //newNFA.alphabet += (*this).alphabet;
    newNFA.alphabet += other.alphabet;

    std::string newStart ="s";
    
    while((*this).has_state(newStart))
    {
        newStart = "s" + newStart;
    }
    newNFA.all_states.insert(newStart);
    for(std::string q : other.all_states)
    {
        std::string old = q;
        while((*this).has_state(q))
        {
            q = "q" + q;
        }
        newNFA.all_states.insert(q);
        tempNFA.update_state_name(old,q);
    }

    for(std::string q : other.final_states)
    {
        while((*this).has_state(q))
        {
            q = "q" + q;
        }
        newNFA.final_states.insert(q);
    }

    /*for(std::string q: (*this).final_states)
    {
        newNFA.final_states.insert(q);
    }
    for(std::string q: tempNFA.final_states)
    {
        newNFA.final_states.insert(q);

    }
    for(std::string q: (*this).all_states)
    {
        newNFA.all_states.insert(q);
    }
    for(std::string q: tempNFA.all_states)
    {
        newNFA.all_states.insert(q);
    }*/

    //newNFA.transitions += (*this).transitions;
    newNFA.transitions += tempNFA.transitions;
    newNFA.transitions.add_rule(newStart,'e',(*this).starting_state);
    newNFA.transitions.add_rule(newStart,'e',tempNFA.starting_state);


    newNFA.starting_state = newStart;
    return newNFA;
}